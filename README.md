Black Diamonds: Reusable Components for Truffle Languages
=========================================================

<img src="https://github.com/SOM-st/black-diamonds/raw/master/docs/sliced-black-truffle-by-mortazavifar-ccbysa40.jpg" align="right" width="200" height="170" alt="Sliced Black Truffles (By Mortazavifar, CC BY-SA 4.0)" title="Sliced Black Truffles (By Mortazavifar, CC BY-SA 4.0)">

The goal of this project is to build a collection of reusable components for
Truffle language implementers.

Ideally, the components remain somewhat independent of each other, and
independent of specific language semantics. However, because of the way they
are developed, there is a certain bias towards languages with simple, uniform,
and consistent semantics.

Thus, the black diamonds might not be applicable directly to your language
implementation. We are happy to further generalize the components and include
changes, but sometimes, things are too different, and it might be easier to
devise a simpler solution specific to a language.


Guidelines and Principles
-------------------------

 - designed to build the "right" thing for today, not to defend against tomorrow
 - there is no backwards compatibility
 - keep things simple
 - to support research
 - the guidelines can change at any point in time


Current Diamonds
----------------

#### 1. Basic: Common concepts for language implementations

The `basic` diamond introduces common concepts language implementations need.
Each of these concepts is used by another diamond, and thus, the `basic`
package forms a common foundation for them.

While it is optional to use all of these concepts, some might be required by
a specific diamond.

#### 2. Settings: Configuring languages and diamonds

The `settings` diamond introduces a minimal framework to configure language
implementations. It is necessary for instance for the primitives diamond to
allow enabling/disabling specific optimizations by users.

#### 3. Primitives: Eager specialization in parser or on first execution

The `primitives` diamond provides support for what we call *eager* specialization.
In many dynamic languages, operators are realized as normal dynamic dispatches.
With this diamond, we add support for doing early specialization, for instance
in the parser, or on the first execution, to select a specific node instead of
performing a function/method activation for each operator.
Of course, fallback mechanisms are provided to ensure correct language semantics.

The benefit of this optimization is to improve interpreter performance, reduce
compilation time, and possibly simplify interpreter debugging since it simplifies
execution drastically.

#### 4. Inlining/Splitting: Support parse-time inlining and run-time splitting

The `inlining` diamond provides infrastructure to support inlining at parse time
and splitting at execution time. Inlining enables us to optimize more complex
structures such as loops, iteration, selection, or other elements that often
take lambdas, closures, or other kind of anonymous methods. If they are provided
lexically, and are trivially non-accessible by language constructs, it can be
very beneficial to inline them on the AST level already to optimize execution
time in the interpreter, which can also reduce compilation time.

This infrastructure provides the basic mechanisms that a language independent.
This includes a general visitor that can adapt lexical scopes for instance also
after simple splitting, which can be necessary, for instance to ensure that
the split methods are independent and specialize independently at run time.

#### 5. Tools

The `tools` diamond provides basic abstractions that are shared by tools.

#### 6. Source

The `source` diamond provides basic abstractions over source code that can
be shared by different languages and diamonds.

License and Acknowledgements
----------------------------

This project heavily relies on the [Truffle] framework developed by Oracle Labs.

The included components are developed in the context of [TruffleSOM], [SOMns], [TruffleMate], and [Grace (SOMns)][grace].

Information on previous authors are included in the AUTHORS file. This code is
distributed under the MIT License. Please see the LICENSE file for details.

[Truffle]: https://github.com/graalvm/graal/tree/master/truffle
[TruffleSOM]: https://github.com/SOM-st/TruffleSOM
[SOMns]: https://github.com/smarr/SOMns
[TruffleMate]: https://github.com/charig/TruffleMate
[grace]: https://github.com/richard-roberts/SOMns/tree/temporary-fixes-for-execution
