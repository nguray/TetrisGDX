
// Source - https://stackoverflow.com/a
// Posted by Anibal Anto
// Retrieved 2025-11-25, License - CC BY-SA 4.0

/*----------------------
Represents an operation
that accepts two input
arguments and returns no
result.
*/
BiConsumer<T,U>         (T x, U y)  -> ()


/*----------------------
Represents a function
that accepts two arguments
and produces a result.
*/
BiFunction<T,U,R>       (T x, U y)   -> R z


/*----------------------
Represents an operation
upon two operands of the
same type, producing a
result of the same type
as the operands.
*/
BinaryOperator<T>       (T x1, T x2) -> T x3


/*----------------------
A task that returns a
result and may throw an
exception.
*/
Callable<V>             ()    -> V x   throws ex


/*----------------------
Represents an operation
that accepts a single
input argument and returns
no result.
*/
Consumer<T>             (T x)   -> ()


/*----------------------
Represents a function that
accepts one argument and
produces a result.
*/
Function<T,R>           (T x)   -> R y


/*----------------------
Represents a predicate
(boolean-valued function)
of one argument.
*/
Predicate<T>            (T x)   -> boolean


/*----------------------
Represents a portion of
executable code that
don't recieve parameters
and returns no result.
*/ 
Runnable                ()    -> ()


/*----------------------
Represents a supplier of
results.
*/
Supplier<T>             ()      -> T x

/*----------------------
Represents an operation 
on a single operand that
produces a result of the
same type as its operand.
*/
UnaryOperator<T>        (T x1)  -> T x2
