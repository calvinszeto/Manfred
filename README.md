# MANFRED

## Actions

Actions are stored in the assets/actions.xml file. Call Action#loadActions to load the actions into
the action data structures. Similar to the log, this needs to be done on a per save basis - the
actions specify whether they are locked/unlocked.

Locked/unlocked information is stored by a single integer. Each bit of the integer refers to the
corresponding action in the actions ArrayList; 1 means locked, 0 means unlocked. This means changes
to the actions.xml file must preserve the existing order. There may be better ways to do this, perhaps
by checking the required stats on import; however, this way is faster and simpler for the beta.

## Todos (Priority)

### General Android
* Check that all lifetime cycle stuff is handled (Medium)
* Add a home link to the Manfred screen (Low)
* Add action category fragment to action item layout (Low)

### Gameplay
* Add delays to the events (High)
* Add support for locked/unlocked actions (High)
* Add stat requirements to actions and action buttons (High)
* Add the instructional overlay (Medium)

### The Game Engine
* Add major events (High)
* Come up with the story (Medium)
* Make a better algorithm for deciding stats changes (Medium)
* Add habitual events (Medium)
