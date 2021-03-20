module Main where

import System.Environment
import System.Exit
import System.Process
import System.IO
import Control.Monad
import Data.Char

solver :: String
solver = "z3 -in"

newtype Cell = Cell { getCell :: (Int, Int) }

data Ruleset = Ruleset
  { size :: Int 
  , constraints :: [(Cell, Cell)] -- Cell1 > Cell2
  , filledCells :: [(Cell, Int)]
  } deriving Show

strPuzzle = unlines
  [ "v # # #"
  , "v ^ # ^"
  , "# # # #"
  , "# 3 # <"
  , ""
  ]

rset = Ruleset
  { size = 4
  , constraints = [(Cell (1,1), Cell (2,1))]
  , filledCells = [(Cell (4,1), 3)]
  }

instance Show Cell where
  show (Cell (x, y)) = "cell_" ++ show x ++ "_" ++ show y

parseRuleset :: String -> Maybe Ruleset
parseRuleset str = let str' = filter (not . null) . map words . lines $ str in
  parseRuleset' (1,1) str' (Ruleset{ size = 0, constraints = [], filledCells = [] })
  where
    parseRuleset' :: (Int, Int) -> [[String]] -> Ruleset -> Maybe Ruleset
    parseRuleset' (x,y) [[]] ruleset 
      | (x-1) == y = Just ruleset{ size = y }
      | (x-1) /= y = Nothing
    parseRuleset' (x,y) ([]:ls) ruleset =
      parseRuleset' (1,y+1) ls ruleset
    parseRuleset' (x,y) (("#":l):ls) ruleset =
      parseRuleset' (x+1,y) (l:ls) ruleset
    parseRuleset' (x,y) (((c:cs):l):ls) ruleset@Ruleset{constraints = cts, filledCells = filledCells}
      | c == '<' = parseRuleset' (x,y) ((cs:l):ls) ruleset{constraints = (Cell (x,y), Cell (x-1,y)):cts}
      | c == '>' = parseRuleset' (x,y) ((cs:l):ls) ruleset{constraints = (Cell (x,y), Cell (x+1,y)):cts}
      | c == '^' = parseRuleset' (x,y) ((cs:l):ls) ruleset{constraints = (Cell (x,y), Cell (x,y-1)):cts}
      | c == 'v' = parseRuleset' (x,y) ((cs:l):ls) ruleset{constraints = (Cell (x,y), Cell (x,y+1)):cts}
    parseRuleset' (x,y) (([]:l):ls) ruleset = 
      parseRuleset' (x+1, y) (l:ls) ruleset
    parseRuleset' (x,y) ((c:l):ls) ruleset@Ruleset{filledCells = filledCells} =
      case (reads :: ReadS Int) c of
          ((val,_):_) -> parseRuleset' (x+1,y) (l:ls) ruleset{filledCells = (Cell (x,y), val):filledCells}
          _ -> Nothing
    

showAsSMT :: Ruleset -> String 
showAsSMT Ruleset { size = size , constraints = constraints , filledCells = filledCells } =
  unlines
    [ "(set-logic QF_AUFLIA)"
    , "; Declaration of constants"
    , unlines ["(declare-const " ++ show (Cell (x,y)) ++ " Int)" | x <- [1..size], y <- [1..size]]
    , "; Setting bounds for constants"
    , unlines [ concat [ "(assert (and (<= 1 " 
                       , show (Cell (x,y))
                       , ") (<= "
                       , show (Cell (x,y))
                       , " "
                       , show size, ")))"
                       ] | x <- [1..size], y <- [1..size]]
    , "; Distinct lines"
    , unlines ["(assert (distinct " ++ unwords [show (Cell (x,y)) | y <- [1..size]] ++ "))" | x <- [1..size]]
    , "; Distinct collumns"
    , unlines ["(assert (distinct " ++ unwords [show (Cell (x,y)) | x <- [1..size]] ++ "))" | y <- [1..size]]
    , "; Constraints"
    , unlines $ map (\(c1, c2) -> "(assert (> " ++ show c1 ++ " " ++ show c2 ++ "))") constraints
    , "; Fixed numbers"
    , unlines $ map (\(c, val) -> "(assert (= " ++ show val ++ " " ++ show c ++ "))") filledCells
    , "(check-sat)"
    , "(get-value (" ++ unwords [ show (Cell (x,y)) | y <- [1..size], x <- [1..size]] ++ "))"
    ]
   

parseArgs :: [String] -> IO (String, [Handle])
parseArgs [] =
  putStrLn "Usage: futoshiki [input_file] [output_file]" >> exitSuccess
parseArgs (in_path:args) = do
  in_str <- openFile in_path ReadMode >>= hGetContents
  out_handles <- mapM (`openFile` WriteMode) args
  return (in_str, out_handles)

main :: IO ()
main = do
  (str, handles) <- getArgs >>= parseArgs
  case parseRuleset str of
    Just ruleset -> do 
      solvePuzzle ruleset (stdout:handles)
    Nothing -> 
      putStrLn "Could not parse file."

solvePuzzle :: Ruleset -> [Handle] -> IO ()
solvePuzzle ruleset outs = do
  let cproc = (shell solver){ std_in = CreatePipe , std_out = CreatePipe }
  (Just hIn, Just hOut, _, hProc) <- createProcess cproc

  putStrLn "Spawned solver process."

  hPutStr hIn (showAsSMT ruleset)
  hClose hIn
  (satisfiability:output) <- fmap lines (hGetContents hOut) 

  case satisfiability of
    "unsat" ->
      putStrLn "Problem is unsatisfiable."
    "sat" -> do
      putStrLn "Problem is satisfiable."
      let values = map ((read :: String -> Int) . tail . dropWhile (/=' ') . dropWhile (==' ') . filter (`notElem` "()")) output
      mapM_ (\h -> mapM_ (hPutStrLn h . unwords)  (chuncksOf (size ruleset) (map show values))) outs
      mapM_ hClose outs
      waitForProcess hProc
      return ()

chuncksOf :: Int -> [a] -> [[a]]
chuncksOf _ [] = []
chuncksOf n l = let (h,t) = splitAt n l in 
  h : chuncksOf n t
