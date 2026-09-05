# Test Plan

Sunrise Dental Clinic — Appointment and Patient Management System

This document covers three things at once, because in practice they happened
together: the automated test suite that now lives under `src/test/java`, the
reasoning behind what got tested and what didn't, and an honest account of
how test-driven development actually entered the picture in this project —
which was not from day one, and not in the textbook order either.

---

## 1. How TDD actually happened here (and it's worth being honest about this)

Textbook TDD is red → green → refactor, written *before* the feature exists.
That is not how this application got built. It got built the way most small
systems actually get built under time pressure: layer by layer, compiled
after every change, and then handed to a real user (you) who clicked through
it and pasted back real stack traces the moment something broke. Four bugs
came in exactly that way during this project:

1. `SQLIntegrityConstraintViolationException` on `dentist_id` — appointments
   couldn't be booked at all for a normal dentist.
2. `Duplicate entry ''` on `patient.PRIMARY` — a silent validation gap let an
   appointment save with no patient id.
3. `StringIndexOutOfBoundsException` in `generateNextId()` — the id-generator
   choked on a leftover malformed row.
4. `IllegalArgumentException: Multiple decimal separators` — every bill and
   every revenue report crashed on the currency pattern.

None of those had a test written first. They were found by a person using
the app, not by a red test. That's a real gap, and pretending otherwise
would defeat the point of writing this document.

What *is* honestly TDD, and what this test suite actually is, is the second
half of the cycle applied **retroactively and then kept**: for the one bug
where it was safe and cheap to do properly (the JasperReports currency
crash, bug #4 above), the fix was re-verified by deliberately re-breaking it,
watching the test fail with the user's exact exception, and only then
re-applying the fix and watching it turn green. Section 5 below is the
actual transcript of that, not a reconstruction. For the other three bugs,
a regression test now exists (some runnable today, one requiring a live
database — see the traceability table in Section 6) precisely so that if
anyone edits this code again, the next occurrence of the same mistake is
a red test in a few seconds, not a stack trace pasted from a running app an
hour later.

Going forward, that's the actual, practical TDD discipline this project
should keep: **when a bug is reported, the first thing written is a test
that reproduces it — confirmed red — before touching the fix.** Not because
it's procedurally correct, but because a red test that matches the reported
symptom is proof the root cause was found, not just papered over. A fix that
"seems to work" and a fix that turns a specific red test green are two
different levels of confidence, and this project has had at least one bug
(the `dentist_id` mix-up) that "seemed to work" for a while because a
dentist's `user_id` and `dentist_id` happened to be equal in the seed data.

---

## 2. Test rationale — why these areas and not others

The suite is deliberately small and deliberately narrow. Four files, thirty
nine automated tests, one opt-in integration test. The scoping logic was:

- **Test what caused a real, reported failure.** Every test class in this
  suite traces back to something that actually broke during this project,
  not a hypothetical edge case. That's what "test rationale" means here —
  not "high coverage for its own sake," but "the thing that broke, pinned
  down so it can't break the same way twice."
- **Prefer pure logic over anything that needs JavaFX or MySQL running.**
  `AppointmentModel`'s cost/duration/overlap math, `Validations`' regex
  patterns, and `PasswordUtil`'s hashing are all plain Java with no
  dependency on a live database or a rendered UI — they can be constructed
  with `new` and asserted on directly. That's precisely why those methods
  were written as instance/static methods on the model in the first place
  (see `docs/ClassDiagram.md`'s note on `TreatmentTypeModel`), and it's what
  makes them cheap to test in isolation.
- **Where a fix genuinely can't be tested without a database, say so and
  isolate it, rather than skip it silently.** The `dentist_id` bug can only
  be proven fixed by actually inserting a dentist/appointment row and
  reading back what MySQL's foreign key actually accepted — a unit test
  faking that out would just be testing the fake. That test exists
  (`AppointmentDentistIdIT`), but it's named `*IT` specifically so
  `mvn test` skips it by default and it never touches your real clinic data
  without you choosing to run it (Section 4 explains how and why).
- **Don't test JavaFX controllers directly.** `RegistrationFormController`'s
  missing "is the id empty?" check (bug #2) is a real gap, but the fix lives
  inside an `@FXML` event handler tied to live `JFXTextField`s, which needs
  a running JavaFX toolkit to instantiate at all. Retrofitting that into an
  automated headless test would mean either a heavyweight TestFX dependency
  or faking the whole FXML tree — both disproportionate to one `if` check.
  Section 6 records this honestly as a documented gap rather than quietly
  leaving it untested and uncommented on.

---

## 3. Test plan

| Test ID | Class | Type | What it checks | Test data | Expected result |
|---|---|---|---|---|---|
| VAL-01 | `ValidationsTest.patientPattern_acceptsCurrentFormat` | Unit | Patient Id regex accepts the current `PT###` format | `PT001`, `PT002`, `PT999` | matches |
| VAL-02 | `ValidationsTest.patientPattern_rejectsInvalidValues` | Unit | Patient Id regex rejects the empty string, the retired `PAT0001` format, and near-misses | `""`, `PAT0001`, `PT01`, `PT0001`, `pt001`, `PT-001` | does not match |
| VAL-03 | `ValidationsTest.dentistPattern` | Unit | Dentist Id regex shape | `DEN001` / `DEN12` / `DEN0012` | true / false / false |
| VAL-04 | `ValidationsTest.appointmentPattern_acceptsGeneratedShape` | Unit | Appointment No regex matches what `AppointmentDAOImpl.generateNextId()` produces | `APT0001`, `APT1` | matches / doesn't |
| VAL-05 | `ValidationsTest.mobilePattern_acceptsRealNumbers` | Unit | Sri Lankan mobile/landline formats used as sample data across the app | `0771234567`, `+94771234567`, `94771234567`, `0094771234567`, `0112345678` | all match |
| VAL-06 | `ValidationsTest.mobilePattern_rejectsGarbage` | Unit | Obviously invalid numbers | `12345`, `077123456`, `abcdefghij` | none match |
| VAL-07 | `ValidationsTest.nicPattern_acceptsBothEras` | Unit | Old (9 digits + V/X) and new (12 digit) Sri Lankan NIC formats | `991234567V`, `199912345678` | both match |
| PWD-01 | `PasswordUtilTest.hash_matchesSeededAdminAccount` | Unit | `PasswordUtil.hash("admin123")` equals the exact hash `schema.sql` seeds for the admin account | `"admin123"` | `240be518fabd...c720a9` |
| PWD-02 | `PasswordUtilTest.hash_isDeterministic` | Unit | Same input → same hash | `"SamePassword1"` (hashed twice) | equal |
| PWD-03 | `PasswordUtilTest.hash_differentInputsGiveDifferentHashes` | Unit | Different inputs → different hashes | `"SamePassword1"` vs `"SamePassword2"` | not equal |
| PWD-04 | `PasswordUtilTest.hash_isSixtyFourLowercaseHexChars` | Unit | Output shape fits the `password_hash VARCHAR(64)` column | any string | 64 lowercase hex chars |
| APT-01 | `AppointmentBusinessLogicTest$CostAndDuration.treatmentCost_scalesPerTooth` | Unit | Per-tooth cost scales with tooth count | Filling (Rs. 6000/tooth) × 3 | Rs. 18,000 |
| APT-02 | `...treatmentCost_flatFeeIgnoresToothCount` | Unit | Flat-fee treatment ignores tooth count | Checking (Rs. 0 flat) × 5 | Rs. 0 |
| APT-03 | `...duration_scalesPerTooth` | Unit | Duration scales with tooth count | Filling (45 min/tooth) × 3 | 135 minutes |
| APT-04 | `...endTime_isStartPlusDuration` | Unit | End time = start + duration | 09:00 start, 135 min | 11:15 |
| APT-05 | `...consultationFee_comesFromDentist` | Unit | Consultation fee is read from the dentist object, never typed | Dentist fee Rs. 1500 | Rs. 1500 |
| APT-06 | `AppointmentBusinessLogicTest$ClashDetection.overlaps_trueForOverlappingScheduledSlots` | Unit | Two SCHEDULED appointments with overlapping ranges clash (symmetric) | 09:00–11:15 vs 10:00–10:15, same day | clash both ways |
| APT-07 | `...overlaps_falseForAdjacentSlots` | Unit | Back-to-back slots (end == next start) don't clash | 09:00–09:15 then 09:15–09:30 | no clash |
| APT-08 | `...overlaps_falseForDifferentDates` | Unit | Same time, different day never clashes | same time, consecutive days | no clash |
| APT-09 | `...overlaps_falseWhenOneSideIsCancelled` | Unit | A CANCELLED appointment stops blocking its old slot | overlapping times, one CANCELLED | no clash |
| APT-10 | `...overlaps_falseWhenOneSideIsNoShow` | Unit | A NO_SHOW appointment stops blocking its old slot | overlapping times, one NO_SHOW | no clash |
| RPT-01 | `ReportRenderingTest.billReport_fillsWithoutThrowing` | Unit (resource-only) | `bill_report.jrxml` fills without the currency-pattern crash | full bill param set (see Section 5) | fills, ≥1 page |
| RPT-02 | `ReportRenderingTest.revenueReport_fillsWithoutThrowing` | Unit (resource-only) | `revenue_report.jrxml` fills, including its TOTAL summary band | 2 `RevenueRow`s (Jan/Feb) | fills, ≥1 page |
| APT-IT-01 | `AppointmentDentistIdIT.savedAppointment_usesDentistIdNotUserId` | **Integration — needs live MySQL, not run by `mvn test`** | Saved `appointment.dentist_id` matches the dentist's own id, not their user id | dentist with `user_id="IT_USR1"`, `dentist_id="IT_DEN1"` (deliberately different) | stored `dentist_id == "IT_DEN1"` |

39 tests run automatically with `mvn test`; 1 additional integration test
exists and is documented but intentionally not wired into the default run
(Section 4).

---

## 4. Test data

Test data was chosen to be realistic rather than arbitrary, so a failure
reads the same way a real clinic scenario would:

- **Treatments** mirror the actual seed rows in `schema.sql`: `FILLING`
  (per-tooth, Rs. 6000, 45 min) and `CHECKING` (flat, Rs. 0, 15 min) — the
  two ends of the per-tooth/flat-fee split that `getTreatmentCost()` and
  `getDurationMinutes()` branch on.
- **The admin password hash** (`PWD-01`) is not a made-up SHA-256 value —
  it's copied verbatim from the `INSERT INTO user` seed statement in
  `schema.sql`, so this test doubles as a guarantee that the documented
  admin/admin123 login actually works against what `PasswordUtil` produces.
- **NIC and mobile numbers** use realistic Sri Lankan formats (old NIC
  `991234567V`, new NIC `199912345678`, mobile `0771234567` /
  `+94771234567`), matching what `PatientModel`/`RegistrationFormController`
  actually expect a receptionist to type in.
- **Appointment dates in the clash-detection tests** are fixed
  (`2026-09-10`, `2026-09-11`) rather than `LocalDate.now()`, so the tests
  are deterministic regardless of what day they're run on.
- **The integration test's ids** are prefixed `IT_` and are values that
  cannot collide with real generated ids (`PT###`, `DEN###` never produce
  `IT_...`), and it deletes every row it inserts in `@AfterEach` — it is
  written to be safe to run against a real development database without
  leaving anything behind, though it still isn't run automatically.

---

## 5. Applying the test plan: a real red → green cycle

This is the one place in this session where the full TDD loop was actually
carried out, not just described. Before writing this document, the currency
pattern fix (`'Rs.' #,##0.00`) was temporarily reverted back to the original
broken pattern (`Rs. #,##0.00`) and the test was re-run to confirm it fails
for the *same reason* the user's app crashed — not a different, coincidental
reason.

**Red** (`mvn test -Dtest=ReportRenderingTest`, pattern reverted):

```
Tests run: 2, Failures: 2, Errors: 0, Skipped: 0, Time elapsed: 1.568 s <<< FAILURE!
report.ReportRenderingTest.revenueReport_fillsWithoutThrowing -- Time elapsed: 1.399 s <<< FAILURE!
org.opentest4j.AssertionFailedError: revenue_report.jrxml must render, including its TOTAL summary band
  ==> Unexpected exception thrown: java.lang.IllegalArgumentException:
      Multiple decimal separators in pattern "Rs. #,##0.00"
        at java.base/java.text.DecimalFormat.applyPattern(DecimalFormat.java:3521)
        at net.sf.jasperreports.engine.util.DefaultFormatFactory.createNumberFormat(...)
        ...
report.ReportRenderingTest.billReport_fillsWithoutThrowing -- Time elapsed: 0.142 s <<< FAILURE!
org.opentest4j.AssertionFailedError: bill_report.jrxml must render a bill without a DecimalFormat error
  ==> Unexpected exception thrown: java.lang.IllegalArgumentException:
      Multiple decimal separators in pattern "Rs. #,##0.00"
```

That is the exact exception type, message, and call stack the user pasted
from the running application. The test reproduces the real bug, not a
lookalike.

**Fix reapplied** (`'Rs.' #,##0.00` in `bill_report.jrxml` and
`revenue_report.jrxml`):

**Green** (`mvn test`, full suite):

```
Test set: report.ReportRenderingTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.776 s
```

All 39 automated tests pass:

```
model.AppointmentBusinessLogicTest$ClashDetection  - Tests run: 5, Failures: 0
model.AppointmentBusinessLogicTest$CostAndDuration - Tests run: 5, Failures: 0
report.ReportRenderingTest                         - Tests run: 2, Failures: 0
util.PasswordUtilTest                              - Tests run: 4, Failures: 0
util.ValidationsTest                               - Tests run: 23, Failures: 0
```

---

## 6. Traceability: bug reported → test that guards it

| Bug (as reported by the user) | Root cause | Test(s) | Runs by default? |
|---|---|---|---|
| `SQLIntegrityConstraintViolationException` on `dentist_id` when registering an appointment | `AppointmentDAOImpl` bound the dentist's `user_id` into the `dentist_id` column instead of their actual `dentist_id` | `APT-IT-01` | No — needs a live database, see Section 4 |
| `Duplicate entry ''` for `patient.PRIMARY` | `RegistrationFormController.registerBtnOnAction` never checked that `patientIdTxt` was non-empty before saving | *(none — see Section 2's honest note on JavaFX controller gaps)* | — |
| `StringIndexOutOfBoundsException` in `generateNextId()` | A malformed/empty `patient_id` row broke the `ORDER BY ... LIMIT 1` id lookup | `VAL-01`, `VAL-02` cover the id *format* the generator now filters for (`^PT[0-9]{3}$`); the DB-level self-healing query itself is not exercised without a live database | Format checks: yes. DB behaviour: no |
| `IllegalArgumentException: Multiple decimal separators in pattern "Rs. #,##0.00"` | `DecimalFormat` reads the literal `.` in `"Rs."` as a second decimal separator | `RPT-01`, `RPT-02` | Yes |

The patient-id gap is the honest weak spot in this plan, and it's called out
rather than hidden: fixing it properly (in a way an automated test can drive)
would mean extracting the "do we have a usable patient id yet?" check out of
`registerBtnOnAction` into a small pure method the controller calls — at
which point it becomes exactly as testable as everything in
`AppointmentBusinessLogicTest`. That refactor wasn't done here because it
touches working UI code for a coverage gain alone with no other reported
symptom, which is scope the user didn't ask for. It's recorded here so the
gap doesn't quietly become invisible.

---

## 7. Running the tests

```bash
# everything mvn test picks up by default (39 tests, no database required)
mvn test

# just one class while iterating
mvn test -Dtest=ReportRenderingTest

# the one integration test, deliberately opt-in - needs a MySQL instance
# reachable with the same credentials util.DBConnection uses, and it will
# insert and then delete rows prefixed IT_ in user/dentist/patient/appointment
mvn test -Dtest=AppointmentDentistIdIT
```

Test results land in `target/surefire-reports/*.txt` after either command.
