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
