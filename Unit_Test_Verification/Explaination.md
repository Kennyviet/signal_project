## Code Coverage Description
The following parts of the code base remained untested throughout the testing process:

- **triggerAlert() in AlertGenerator.java** — this method only prints to System.out so no assertion was made on its output. The assignment needs extra testing setup which includes testing console output that requires special equipment.

- **FileDataReader.java** — the tests failed because the tests needed actual files on the disk and a complete directory setup which created challenges for standard unit testing that needed additional mocking tools.

- **ECG alert logic in AlertGenerator.java** — the system needs 10 data points to start the sliding window calculation which makes it difficult to create a basic test setup.