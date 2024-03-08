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

