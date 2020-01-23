# dijkstras-shortest-path
Solution I devised for a Project like Interview question.

## Question

There are two lines of input

You are given a space separated input array of weighted edges of the format [A,B,10] where A is the source node, B the destination node and 10 the weight of the edge connecting A to B and a second input line of the format A->B,8 where A is the starting node, B is the destination node and 8 is the maximum amount of time allowed to reach B from A.

Your program must output the shortest path from a source Node to a Destination Node.

The first line of input will have no leading or trailing whitespace. A correctly formed line could look like so:
```
[A,B,2] [B,C,1] [C,D,3]
```

The second line will also have no leading or trailing whitespace. The format will be a Capital letter representing the source node, separated by '->' and then another capital letter representing the Destination node, then a comma ',' followed by an Integer representing the maximum permissable time. A correctly formatted input could look like so:
```
A->D,10
```

There are three errors that need to be reported via the STDOUT

- E1: An Input Syntax Error
- E2: A Logical Input Error
- E3: Unable to find a suitable route

To report the error, print the error code, "E1", "E2" or "E3" respectively
