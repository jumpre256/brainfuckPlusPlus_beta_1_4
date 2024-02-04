# Brainfuck++
#### Version beta 1.4

Brainfuck language, by Urban Müller, uses the 8 single-character operations `<>+-[],.` to constitute, although impractical, a complete programming language. This extension, a superset of brainfuck, adds 44 new characters:  
`:;|#*^{}`, `a` to `z`, and `0` to `9`,  
to provide just a few new programming features. 

To start, if not already, one should be familiar with brainfuck. There are many resources for learning this, one is: [Basics of BrainFuck](https://gist.github.com/roachhd/dce54bec8ba55fb17d3a).

For the following "`1. Main operators and characters new to brainfuck++:`" section of text we will use:  
- `0` for any positive integer or zero, i.e., ... `114`, `1023`, etc., up to positive infinity.  
- `a` for any (lowercase) character `a` to `y`, i.e., not `z`.

#### 1. Main operators and characters new to brainfuck++:
- Locator `:z0` (read "set locator z `0`")  
For basic programs, the user is encouraged to place one of these operators at the end of their program. Using `;z0` (where the `0` matches the `0` in "`:z0`") loads all the `:a` locators between the starting `;z0` and ending `:z0`. Essentially allowing assembly-like sections in brainfuck++'s own quirky way...
- Locator `:a` (read "set locator `a`")  
This operator is like an assembly label. When `:a` is placed between a `;z0` and `:z0` pair, within the program it can be jumped to with a `;a` or `|a` operator.
- Operator `|a`  
Jumps immediately to the corresponding `:a`. Can be thought of as a "branch-always" assembly instruction.
- The `#` character  
Can be used to write a single-line comment that is terminated by ending the line of code with the return key.
- Operators `$` and `;a`  
I feel these are best communicated through some example code.
```
#a complete example brainfuck++ program.
;z1 #load locator 'a' so it can be used. Required.
a:  #in this case code below "locator_a" represents (continued)
#[...] a sub-program that does something
-[----->++<] #any code can go here, in this case
#          the code increments the next cell by 102.
$       #this operator represents "return"

;a  #"calls" a, execution-pointer goes to locator-a

#and returns here when it hits the "$" operator.
#With using ";a" you should always include a "$" at  
#end of the method code. Proceed to not do so  
#at your own peril.

:z1     #remember this symbol at the end of the program.
```

#### 2. Final 2 operators and final 2 characters of brainfuck++:
Finally, we look at `*^` and then `{}`.

In brainfuck++, in addition to the array of memory provided by regular brainfuck, this language provides an additional 1 single byte of variable memory, I like to call the "`active vault`" (or `AV` for short).

- Operator `^` (read "set `AV`")  
Copies the value in the current cell into the `active vault`.

- Operator `*` (read "get `AV`")  
Copies the value from the `active vault` into the current cell.

- Characters `{` and `}`  
Simply for multi-line comments. Multi-line comments begin with `#{` and end with `}#`. Nested multi-line comments are not supported/allowed.

#### 3. Motivation:
Brainfuck++ adds a few "quality of life" additions to the original brainfuck language whilst keeping in the spirit of its esoteric, hardcore nature.  
Designed for building bigger projects in the future in a brainfuck-esque language for the memes. Goal is to one day make a brainfuck++ powered operating system.
Implemented in Java. Goal is to hide this implementation detail more in the future.

#### 4. Code structure:
Is put on a github directly from being an intelliJ project. But it is very easy to grab the source java code files from the 'src' folder and compile and run them without intelliJ.

#### 5. Date(s):
Plan to improve the front-end user interface of this software with a deadline of 12th February 2024;

#### 6. Example code
#### TODO: write a hello world program in brainfuck++ here.

#### 7. Project TODOs:
Many TODOs, namely;  
-> provide easier compiling and executing support for new users.  
-> provide instructions here on how to use the software. 
