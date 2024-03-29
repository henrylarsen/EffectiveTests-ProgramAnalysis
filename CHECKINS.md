Use this file to commit information clearly documenting your check-ins' content. If you want to store more information/details besides what's required for the check-ins that's fine too. Make sure that your TA has had a chance to sign off on your check-in each week (before the deadline); typically you should discuss your material with them before finalizing it here.

# Check-in 1

We decided to try and take the requirements (b) and (c) route,
using a popular language alongside some interesting visualization,
taking advantage of some viz. background that Harry and Mazen have
from CPSC 447. This means our ideas will be focused around dynamic
program analysis rather than static

We have two main ideas we discussed:
1. Garbage collection memory analysis
   1. Correlating time graph / stack with memory pressure, GC zones, etc.
   2. In terms of requirements, biggest question is around how to make a
project like this sufficiently control-flow sensitive, as examples like
the flame graph, although representing the result of control flow, are not
considered control flow sensitive in and of itself. Perhaps we can consider
representing the memory pressure / allocations as a result of branching in
a method of interest? TBD.
2. Advanced coverage / Code hotspots
   1. Dynamically capturing coverage and how often lines of code have been
reached, visualized in an interesting way perhaps with a control flow diagram.
We may also consider inter-method/class relationships and represent execution flow
in this way.
   2. In terms of requirements, this idea would handle control flow sensitivity
more directly as it requires execution analysis on a line-by-line basis. This comes
at the cost of more intricate implementation details to do such an analysis on a
popular language.

Next steps:
- define in more detail each candidate idea to better test these against the
requirements
- select a candidate idea and research the tooling available for various popular
languages
- look at real world use cases provided by Quanming to see how our idea matches
with other tools out there!

# Check-in 2

## Overview
We have changed direction from our check-in 1 plan and will now be building a 
program analysis tool that will provide information on assertion-based test
coverage of visible effects from a given method.

Given a specified method and the corresponding tests written to cover that 
method, the tool will inform the user which effects of the method have been 
covered with assertions by the tests and which effects remain uncovered. The method will 
be specified by name and file path and the related tests by annotation.

The tool will statically analyze the possible paths through a method and the effects
given each path, which are control flow sensitive. The covered effects per method invocation
within a test will be determined dynamically. After execution, the user will be 
provided with a coverage score equal to the ratio of covered effects to total effects
given the path.

The overall goal is to provide the user with a better understanding of fields that 
may have been modified but have not been tested for assertion given an execution.

### Effects covered:
- Return values
- First-level changes to an object
- Variable reassignment
- Changes to lists

### Stretch goals:
- Remove annotation step - have the tool suggest relevant tests which the user can add/remove tests from
- Cover deeper effects within the objects (e.g. global variables)

## Timeline

Weekly team check-ins Mondays 5-6pm (hybrid, location TBD)
- Monday, March 11th:
  - solidify technical design
  - start research and implementation on least contentious pieces (likely won't be changed by user studies)
- Tuesday, March 12 - User study 1 designed
- Thursday, March 14 - User study 1 complete, findings ready for check-in
-  **Friday, March 15 - Friday, March 29: Two-week sprint for MVP implementation**
- Sunday, March 31 - MVP, user study 2
- Friday, April 5 - Testing, user study changes
- Saturday, April 6 - Video complete
- Sunday, April 7 - Video submitted

Check-ins involve reporting on progress, raising blocking issues, and coming up for solutions to stay on track. 
Team will collaboratively determine the best course of action in these meetings to meet deadlines. For features 
involving multiple people, designs, tasks, and division of workload will be negotiated between them and reported back 
to the overall team. The expectation for these 'sub-teams' as they regularly communicate and are responsible for each 
other.

## Division of Responsibilities

### AST Visitor API
**Assigned**: Harry

### Static Analysis
**Assigned**: Ron, Louise

### Dynamic Analysis
**Assigned**: Henry, Mazen

### User Studies
**Assigned**: Henry, Louise

### Video
**Assigned**: Harry

### Testing
**Assigned**: All

## Summary of Progress/TA Feedback
Our project is still in the planning stages. Due to TA feedback we have changed direction for our project 
but remain on track in the planning of our program analysis tool.


# Check-in 3

## Summary of Progress/TA Feedback
This week, we discussed our implementation strategy; scaffolded our repository; designed and executed our user study; researched relevant tools and libraries; and began work on our data structures.
During our Thursday meeting, Guanming reaffirmed that the scope and nature of our tool was suitable for our project requirements.
With these tasks completed, we are on track relative to our current project timeline (see Check-in 2).

## Mockups for User Study

As our tool does not contain any visualization aspect, we determined that a critical, qualitative assessment of the tool would be more valuable to our design and development than a task-based study involving interpretation of program output. We discussed this during our TA check-in to confirm this was reasonable.
The following code snippets were provided during our user studies to display different levels of effects coverage:

<details>
<summary>Code Snippets</summary>

```java

public class MaxTracker {
private int highestEven = Integer.MIN_VALUE;
private int highestOdd = Integer.MIN_VALUE;

    // Constructor
    public MaxTracker() {
    }

    // Adds number to track in this class.
    // Returns whether it is higher than the highest even/odd
    public boolean trackNumber(int number) {
        if (number % 2 == 0) { // Check if the number is even
            boolean higher = number > highestEven;
            highestEven = Math.max(number, highestEven);
            return higher;
        } else {
            boolean higher = number > highestOdd;
            highestOdd = Math.max(number, highestOdd);
            return higher;
        }
    }

    public int getHighestEven() {
        return highestEven;
    }

    public int getHighestOdd() {
        return highestOdd;
    }
}

public class TestMaxTracker {

    @Test
    @ComprehensiveTest(
        testClass = MaxTracker.class,
        testMethod = "trackNumber"
    )
    public void testMaxTracker0() {
        MaxTracker tracker = new MaxTracker();
        tracker.trackNumber(2);
    }

    @Test
    @ComprehensiveTest(
        testClass = MaxTracker.class,
        testMethod = "trackNumber"
    )
    public void testMaxTracker50() {
        MaxTracker tracker = new MaxTracker();
        assertTrue(tracker.trackNumber(2));
    }

    @Test
    @ComprehensiveTest(
        testClass = MaxTracker.class,
        testMethod = "trackNumber"
    )
    public void testMaxTracker100() {
        MaxTracker tracker = new MaxTracker();
        assertTrue(tracker.trackNumber(2));
        assertEquals(2, tracker.getHighestEven());
    }
}
```

</details>



### Critical feedback from our users:
- The term “comprehensive” is misleading and limited in its evaluation of test suites
    - It might lead users to assume that a comprehensive test has satisfied testing all relevant paths. We had differentiated the concepts of comprehensive tests and comprehensive test suites, but there may still be confusion and misinterpretation
    - It implies that the tests at hand are complete, though there may still be work to be done (and vice versa for incomprehensive); path and effects coverage are not equal to a quality test suite
- Most useful kinds of effects to cover would be parameter and list/array/field mutations (even just included as a warning)
- Line numbers not strictly necessary; identifiers more useful
- Writing the test name in the annotation is annoying - semantically linked

### Suggestions:
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

# Checkin 4

## Status of Implementation So Far
Most of the data structures / interfaces that we will need have been implemented at this point, meaning that we can 
start on the functionality itself. In particular, the code that has to be injected into the source code has been 
written, so now we can split into our sub-teams and write:
- the code that actually does the injecting (including static analysis component)
- the logic that runs within the injected code
- the orchestration of the tool and its input/output

We also had a discussion with Yanze this week about whether our analysis was control flow sensitive, and it was 
determined it wasn't. Our solution is to perform CFS value agnostic static analysis on user's test code in order to
statically determine what assert statements are checking the values of which getters. For 2 examples of cases this step
must handle, [see this planned user study 2 task](testproject/src/test/java/user/study/TestMaxTracker2.java).

## Plan for final user study
We plan to run the user study with an MVP of our analysis tool on `testproject` within our repo. We will have a mix of 
predictive and task driven questions in the study, 2 of which are already in the repository. We will 
add more tasks as our implementation continues, and we develop questions about how edge cases should be handled, possible 
usability improvements, and other concerns we want addressed by the study.

[See planned user study tasks so far](testproject/src/test/java/user/study)

## Planned timeline for remaining days
Our planned timeline from Checkin 2 is still accurate. With our tasks assigned, we have a sprint until March 31st at 
which point we should have an MVP and be performing user study 2.

# Check-in 5
We have already ironed out the tasks for our final user study, so as soon as we're finished our MVP this weekend, we can run the study on Tuesday/Wednesday next week.
We will have a quick turnaround to address final feedback from the study before Saturday. Our video will be filmed next weekend for a submission on Sunday evening.


