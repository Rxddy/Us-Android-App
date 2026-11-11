# Us (android version)

Android version of our couples app. Java + Android Studio. This is what i got done so far, the rest is
split between us (see "who does what" at the bottom).

The look is copied from the ios app, screenshots are in `docs/ios-reference/` so you can see what we're
going for (dark brown background, orange buttons, serif text for big stuff, little tinted cards).




### how a screen works (use Memories as the example)

- `MemoriesActivity` shows a RecyclerView. data comes from `UsDatabase` through `Background.run(...)`
  so the ui never freezes. a ProgressBar shows while it loads
- the + button opens a custom dialog (`dialog_add_memory.xml`) with EditTexts, a spinner and a date picker.
  save does a Toast, delete does a Snackbar with undo
- tapping a memory opens `MemoryDetailFragment` (next to the list in landscape, new screen in portrait)
- everything is in `res/layout`, strings are in `res/values/strings.xml`, british english overrides are in
  `res/values-en-rGB/strings.xml`
- colors are in `res/values/colors.xml`, theme in `themes.xml`. use those instead of hardcoding colors

copy that pattern for your own section, it covers most of the requirements.

## what's done vs what needs doing

The course says everyone has to do requirements 2 to 11 in their own section (fragment, recyclerview,
detail on tap, database, add/delete, background thread, progress bar, button, edittext, toast + snackbar +
custom dialog). 1, 12 and 13 are shared (main page, help dialog, second language).

| | Memories | Plans | Pings | Our Days |
|---|---|---|---|---|
| fragment | done | TODO, move the detail dialog into a fragment | TODO ping detail fragment | TODO |
| recyclerview | done | done | done | TODO list of special dates (birthdays etc) |
| tap for detail | done | dialog for now | TODO | TODO |
| saved in db | done | done | done | TODO new table |
| add + delete | done | done | TODO delete one ping (only clear all right now) | TODO |
| background thread | done | done | done | TODO use Background.run |
| progress bar | done | done | done | done |
| button | done | done | done | done |
| edittext | done | done | done | TODO |
| toast + snackbar + custom dialog | done | done | TODO custom dialog | TODO custom dialog |

Also each of us needs unit tests + instrumented (espresso) tests for our own section.

## who does what (change it if you want to swap)

- **Memories + home page (req 1):** Yaseen
- **Plans + help dialog (req 12):** bigalthefirst1
- **Pings + second language (req 13):** ______ (make sure every string has a british version)
- **Our Days + class diagram / use case diagram:** ______

other stuff we still need as a group: proposal/charter (needs prof approval before we go further),
iteration 1 submission, test report, presentation + video.

## tips so we dont wreck each other's stuff

- make your own branch for your section and merge it in when it works
- strings.xml, AndroidManifest.xml and UsDatabase.java are the files we'll all touch. if you can, put your
  strings in your own file like `strings_plans.xml` and tell the others before changing shared files
- we're a group of 4 but the brief says 5, might want to ask the prof about that
