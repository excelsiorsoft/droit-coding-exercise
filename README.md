Socrates is a man. All men are mortal. Therefore, Socrates is mortal.

You probably know that a general inference engine may carry out this sort of reasoning by combining the knowledge encapsulated in a rule like `Human(x) -> Mortal(x)` with the knowledge of the fact `Human(Socrates)` to infer `Mortal(Socrates)`.

But in this exercise, rules refer only to concrete propositions given as simple unitary variables, not quantified expressions. The conjunction of all the premises appearing on the left hand side of a rule implies that the single conclusion appearing on the right hand side can be derived. We can represent rules of this form in [EDN](https://github.com/edn-format/edn) like so: `[[:socrates-is-a-man :all-men-are-mortal] :-> :socrates-is-mortal]`.

Given this set of two rules:

`[[:a :b] :-> :c]`
`[[:c :d :e] :-> :f`

If we are given the premises `:a` and `:b`, we can derive `:c` as the only resulting conclusion of the rule system. If we are given `:a`, `:b`, `:d`, and `:e`, we can use the fact that we can derive `:c` to also derive `:f`.

Although a variable on the right-hand side of a rule can appear on the left-hand side of other rules, we only consider variables that appear solely on left-hand sides to be inputs to the rule system as a whole; any variable appearing on a right-hand side is considered an output. (There may be more than one rule that can be used to derive the same output, but you can assume there are no cycles by which an output would contribute to the implication of itself.) Here, `:a`, `:b`, `:d`, and `:e` are the valid inputs to the rule system, and `:c` and `:f` are the possible outputs.

---

**Task 1**. Write a function that takes a set of rules, and partitions all the variables involved into a set of input variables and a set of output variables, according to the above definitions.

**Task 2**. Write a function that performs basic inference on rule systems of this kind. It should take two arguments: a set of rules and a set of input assertions, and it should return the set of all outputs that the rule system can derive from those inputs. You will be asked to develop a more sophisticated approach to inference in task 5, but do take this task seriously.

**Task 3**. The rule system can be thought of as a simple mapping from inputs to outputs, like what you implemented in task 2. But knowledge engineering involves much more than this, and we want to be able to answer more interesting questions about the knowledge embedded in the rule system. Utilize the function from task 1 to write another function that takes a set of rules as its only argument, and returns a data structure that maps every output variable to the smallest set of input assertions that is sufficient to derive it. For the example rule set above, `:f` would map to `[:a :b :d :e]`.

**Task 4**. Write a function that takes a set of rules as its only argument, and returns a data structure mapping each possible set of input assertions to the complete set of inferences that can be derived from it. In the above example, `[:a :b :d :e]` would map to `[:c :f]`.

**Task 5**. In task 2, you wrote a function that performed inference by taking both rules and inputs as arguments. Now you are going to make it a little more interesting by splitting the work into two distinct phases. Write a function that takes a set of rules as its only argument, uses them to create a pretreated data structure somehow, and returns a new function as its result. This resulting function should take a set of input assertions as its only argument, and it should return the set of valid inferences with the aid of what was computed in the pretreatment step.

---

Describe the design choices you have made and the efficiency of your solutions to the above tasks. Going from task 4 to task 5, you may find you wish to rethink how you encode the knowledge embedded in the rule system to balance many competing concerns. Your design decisions can affect the amount of work that must be done by the main function, the amount of work that must be done by the the function it returns, the amount of space required to help build a precomputed data structure, and the size of that precomputed data structure. Tell us about the tradeoffs you made. Consider: Under what input conditions does your implementation provide tractable solutions? How might different tradeoffs be appropriate in different use cases for this system (e.g. for a small set of rules that may be changed often, or a large set of rules to be changed rarely)? Do these aspects of the computation vary significantly if there are many rules with a single premise? One rule with many premises? Many rules, with almost the entire set of input variables being used in every single rule? (You may want to provide some of these situations as test cases with the code you submit.)

The insights you provide are as important as the code you write. The programming language and the particular internal representations you use are up to you, but please provide tests that take their rule inputs from an EDN file so that we can see your code up and running on real examples without much fuss. (Feel free to do more sophisticated testing to increase your confidence in your solutions.)

Suppose that for task 5, instead of merely generating a data structure in order to immediately use it in the same running program, you were to serialize it to a file which is sent to a web client in order to perform inferences from within a browser. (Assume the client-side code can be written in the same language, even if there is no your-chosen-language-to-JS implementation in the real world). Would you change anything about the design of the data structure or the way it is used? (It's fine to just talk about essential logical aspects of the data representation. No trick questions here about any particular fancy compression formats.)

