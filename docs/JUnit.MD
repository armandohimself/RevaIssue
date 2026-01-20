# JUnit

## Assertions - What to Know

assertNotNull(x)

Plain English: “If this is null, stop the show and tell me.”

assertEquals(expected, actual)

Plain English: “These must match exactly.”

assertFalse(condition)

Plain English: “This condition must be false.”

How were you supposed to know these?
You weren’t “supposed to magically know.” These are the core 3–5 you learn first.

Other common ones worth knowing:

assertTrue(...)

assertThrows(Exception.class, () -> ...)

assertAll(...) (group multiple asserts)
