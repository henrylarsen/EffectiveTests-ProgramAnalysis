# User Study 1
This user study was based off of mockups, where the user completed a task on some demo code and then asked for their thoughts

## Participant 1
Feedback:
- The term “comprehensive” is misleading and limited in its evaluation of test suites
  - It might lead users to assume that a comprehensive test has satisfied testing all relevant paths. We had differentiated the concepts of comprehensive tests and comprehensive test suites, but there may still be confusion and misinterpretation
  - It implies that the tests at hand are complete, though there may still be work to be done (and vice versa for incomprehensive); path and effects coverage are not equal to a quality test suite
- Most useful kinds of effects to cover would be parameter and list/array/field mutations (even just included as a warning)
- Line numbers not strictly necessary; identifiers more useful
- Writing the test name in the annotation is annoying - semantically linked
Suggestions:
- Warnings when mutating public static or global fields, independent of coverage
- Consider and therefore annotate test suites per class rather than each test per method
- Allow users to exclude certain fields from analysis
- Should limit to publicly-accessible fields (i.e., with getters)

In light of this feedback, we have made the following changes to our design and timeline:
- Disclude correct line numbers as a criterion for our MVP
- Set ignore flag as a stretch goal
- Limit field coverage to publicly-accessible fields
- Prioritize coverage of parameter mutation (as that was considered the most helpful by both study participants)
- Discuss class-based vs. method-based coverage
- Replace or explain the term “comprehensive”

## Participant 2
4th year UBC CPSC student

Overall:
- Certainly a useful tool, liked the idea of the path-sensitive feedback
- Ensure that documentation is clear that it is only testing the test path taken
  - Do not want user to falsely assume that the “comprehensiveness” extends to more than it is testing (similar to Mazen, “comprehensive” may not be the best adjective)
  - Could add a note at the beginning of each output report about the tools scope?
- Did not think that line numbers would be needed. Could be helpful but the user would not be asserting a method call halfway through – all the info that is needed is that the field has been modified at some point and needs to be asserted.
- Thought that parameter mutation and mutating lists/array/fields would be the most useful addition. Even if used as a warning rather than included in coverage


# User Study 2
The blank user study and its source code can be found [here](testproject/src/test/java/user/study/UserStudy2.md)
## Participant 1
A second year CPSC student and CPSC 210 TA, familiar with java and testing concepts
### Tasks
1. Go to [TestMaxTracker1](TestMaxTracker1.java) and complete the task there
   - The user successfully interpreted our tool's output and used it to successfully complete the exercise
2. Go to [TestMaxTracker2](TestMaxTracker2.java) and complete/answer the 3 tasks there
   - The user quickly found the issue with the first test based off our tools feedback
   - The second one the user did not initially see the issue, but once it was pointed out 
     and the behaviour was explained they said it made sense.
   - The user said the current under-approximation of assertions seems like the best way to handle things,
     and that even though they couldn't tell what was wrong immediately it made sense after the fact, and 
     made more sense than over-approximating instead.

### Q&A
1. You ran our tool in both the command line and with IntelliJ test runner. Which of these methods
   did you prefer, and why?
   - They preferred IntelliJ because of the built-in buttons, but did like the colors in the terminal version
2. Under what circumstances would you be likely to use the tool, if any? When would you be inclined not to use it?
   - They would use it mostly for smaller projects where they think complete coverage would be most feasible.
3. Do you have any suggestions for things we should change about the tool?
   - No
4. Overall, how was your experience with our tool, EffectiveTests?
   - Having successfully completed the tasks, the user said they had fun and liked the tool.


## Participant 2
A fourth year CPSC student, familiar with Java and testing concepts, has completed several internships
### Tasks
1. Go to [TestMaxTracker1](TestMaxTracker1.java) and complete the task there
    - The user successfully interpreted our tool's output and used it to successfully complete the exercise
    - After this task, the user was asked if they thought the test suite covered the class under test in a satisfactory way.
    - The user said yes because all effects were covered (but did not immediately think about the different output partitions--in this case, `true` and `false` return values from `trackNumber`.) One takeaway from this: clear communication about what
      the tool does and doesn't do is essential; returning a success message in green text might guide programmers into thinking
      their test suite is done.
2. Go to [TestMaxTracker2](TestMaxTracker2.java) and complete/answer the 3 tasks there
    - The user found the issue with the first test and fixed it without any assistance.
    - The user did not understand the output of the tool for the second test; after an explanation of static analysis and
      the necessary tradeoffs, they said they understood the behaviour.
    - The user said that our current implementation, to use the intersection of branches rather than the union,
      was more useful and encouraged modular testing.

### Q&A
1. You ran our tool in both the command line and with IntelliJ test runner. Which of these methods
   did you prefer, and why?
    - Preferred running tests in IntelliJ because it was one click rather than switching to a terminal.
    - Asked if our colours could be made available in IntelliJ and said that was a nice feature
      when using the terminal.
2. Under what circumstances would you be likely to use the tool, if any? When would you be inclined not to use it?
    - Would not use it for school projects (which are commonly run against autograders with test suites). 
    - Would be more likely to use it for work for the following reasons:
      - Bugs have more of an impact on others, 
      - Regression testing is more critical, 
      - A greater number of people touch the same code, and 
      - Users are more likely to do unexpected things with a program (as opposed to TAs and standard test suites)
3. Do you have any suggestions for things we should change about the tool?
    - Had no feedback concerning the tool itself, but emphasized thorough documentation to specify tool behaviour (for instances like 2.2)
4. Overall, how was your experience with our tool, EffectiveTests?
    - Said it made writing tests more fun; liked the immediate gratification and progress indicators