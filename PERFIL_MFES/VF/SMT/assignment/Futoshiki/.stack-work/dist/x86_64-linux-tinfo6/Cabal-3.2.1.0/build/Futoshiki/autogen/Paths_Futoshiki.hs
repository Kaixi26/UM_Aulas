{-# LANGUAGE CPP #-}
{-# LANGUAGE NoRebindableSyntax #-}
{-# OPTIONS_GHC -fno-warn-missing-import-lists #-}
module Paths_Futoshiki (
    version,
    getBinDir, getLibDir, getDynLibDir, getDataDir, getLibexecDir,
    getDataFileName, getSysconfDir
  ) where

import qualified Control.Exception as Exception
import Data.Version (Version(..))
import System.Environment (getEnv)
import Prelude

#if defined(VERSION_base)

#if MIN_VERSION_base(4,0,0)
catchIO :: IO a -> (Exception.IOException -> IO a) -> IO a
#else
catchIO :: IO a -> (Exception.Exception -> IO a) -> IO a
#endif

#else
catchIO :: IO a -> (Exception.IOException -> IO a) -> IO a
#endif
catchIO = Exception.catch

version :: Version
version = Version [0,1,0,0] []
bindir, libdir, dynlibdir, datadir, libexecdir, sysconfdir :: FilePath

bindir     = "/home/kaixi/git/UM_Aulas/PERFIL_MFES/VF/SMT/assignment/Futoshiki/.stack-work/install/x86_64-linux-tinfo6/e2449e0e987ab3da8e3c085d5bb8e0ea0a2cb42b970eb5fe1788b72238a7a8c6/8.10.4/bin"
libdir     = "/home/kaixi/git/UM_Aulas/PERFIL_MFES/VF/SMT/assignment/Futoshiki/.stack-work/install/x86_64-linux-tinfo6/e2449e0e987ab3da8e3c085d5bb8e0ea0a2cb42b970eb5fe1788b72238a7a8c6/8.10.4/lib/x86_64-linux-ghc-8.10.4/Futoshiki-0.1.0.0-7dOtVRfUVgYH16LsMfm4u7-Futoshiki"
dynlibdir  = "/home/kaixi/git/UM_Aulas/PERFIL_MFES/VF/SMT/assignment/Futoshiki/.stack-work/install/x86_64-linux-tinfo6/e2449e0e987ab3da8e3c085d5bb8e0ea0a2cb42b970eb5fe1788b72238a7a8c6/8.10.4/lib/x86_64-linux-ghc-8.10.4"
datadir    = "/home/kaixi/git/UM_Aulas/PERFIL_MFES/VF/SMT/assignment/Futoshiki/.stack-work/install/x86_64-linux-tinfo6/e2449e0e987ab3da8e3c085d5bb8e0ea0a2cb42b970eb5fe1788b72238a7a8c6/8.10.4/share/x86_64-linux-ghc-8.10.4/Futoshiki-0.1.0.0"
libexecdir = "/home/kaixi/git/UM_Aulas/PERFIL_MFES/VF/SMT/assignment/Futoshiki/.stack-work/install/x86_64-linux-tinfo6/e2449e0e987ab3da8e3c085d5bb8e0ea0a2cb42b970eb5fe1788b72238a7a8c6/8.10.4/libexec/x86_64-linux-ghc-8.10.4/Futoshiki-0.1.0.0"
sysconfdir = "/home/kaixi/git/UM_Aulas/PERFIL_MFES/VF/SMT/assignment/Futoshiki/.stack-work/install/x86_64-linux-tinfo6/e2449e0e987ab3da8e3c085d5bb8e0ea0a2cb42b970eb5fe1788b72238a7a8c6/8.10.4/etc"

getBinDir, getLibDir, getDynLibDir, getDataDir, getLibexecDir, getSysconfDir :: IO FilePath
getBinDir = catchIO (getEnv "Futoshiki_bindir") (\_ -> return bindir)
getLibDir = catchIO (getEnv "Futoshiki_libdir") (\_ -> return libdir)
getDynLibDir = catchIO (getEnv "Futoshiki_dynlibdir") (\_ -> return dynlibdir)
getDataDir = catchIO (getEnv "Futoshiki_datadir") (\_ -> return datadir)
getLibexecDir = catchIO (getEnv "Futoshiki_libexecdir") (\_ -> return libexecdir)
getSysconfDir = catchIO (getEnv "Futoshiki_sysconfdir") (\_ -> return sysconfdir)

getDataFileName :: FilePath -> IO FilePath
getDataFileName name = do
  dir <- getDataDir
  return (dir ++ "/" ++ name)
