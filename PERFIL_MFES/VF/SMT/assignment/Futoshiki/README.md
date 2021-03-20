# Futoshiki
The puzzles in `examples/` were obtained from [www.futoshiki.org](https://www.futoshiki.org/).

## Running
The program can be run as shown in the example (The solution will be print to stdout and therefore the output file is optional):
`stack run examples/4by4.txt examples/4by4.solution`

## Puzzle file
The puzzle file has the following specification:
* `\n` is used to separate lines;
* space is used to separate collumns between the same line;
* `#` denotes an empty cell;
* `n` where 'n' is an integer denotes a filled cell;
* `<` denotes value on current cell is greater than the value on the cell to the left of it;
* `>` denotes value on current cell is greater than the value on the cell to the right of it;
* `^` denotes value on current cell is greater than the value on the cell to the top of it;
* `v` denotes value on current cell is greater than the value on the cell to the bottom of it;
* commands can be chained for more complex cells, symbols come first and numbers(optionally) after `<>3` indicates the cell is filled with the value 3 and it's value is greater than the values of the cells on the left and the right.
