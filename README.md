# MANFRED

## Stats

The four stats to be measured are:

1. Weight
2. VO2-max (to measure cardiovascular fitness)
3. Squat (to measure strength)
4. Body Fat %

## Actions

### Loading Actions

Actions are stored in the assets/actions.xml file. Call Action#loadActions to load the actions into
the action data structures. Similar to the log, this needs to be done on a per save basis - the
actions specify whether they are locked/unlocked.

Locked/unlocked information is stored by a single integer. Each bit of the integer refers to the
corresponding action in the actions ArrayList; 1 means locked, 0 means unlocked. This means changes
to the actions.xml file must preserve the existing order. There may be better ways to do this, perhaps
by checking the required stats on import; however, this way is faster and simpler for the beta.

### Types of Actions

1. Minor Actions: minor actions are unlocked by reaching one or two stat requirements. Minor actions
    themselves only affect one or two stats: hence, the player needs to use a combination of actions
    to unlock the Major Actions.
    Minor Actions result in two things: a stat change, and a minor event added to the log.

2. Major Actions: these actions progress the story. They require a certain combination of all four
    stats. This way, the player is kinda forced into choosing one of the two paths, and not something
    wildly in-between.
    Major Actions result in two things: a stat change, an image change, a major event in the log, and
    a new set of Actions.

## Unlocking Actions

After an Action is applied, a method runs to re-calculate the locked/unlocked actions. The method
loops through each possible Action - if the Action is on the correct level and the stats requirements
are met, then the Action is unlocked. Afterwards, the database is updated to reflect any changes.

## The Story

The story of Manfred can go in two paths: fit or fat. Regardless of the path chosen, the story has
five major levels:
1. Normal Life: Life is normal. The game begins here.
    Fit Major Event: Run a marathon
    Fat Major Event: Go to the BBQ Festival
2. Civil Unrest: Both major events take Manfred to the park. At the park, riots start breaking out
    in protest of the new Prime Minister. Manfred runs all the way home (fit) or eats all the BBQ
    before the rioters can get to it (fat).
    Fit Major Event: Buy a home gym
    Fat Major Event: Buy a pork butt
3. War: Both major events take Manfred to the store. A bomb goes off in the store; war has officially
    begun. Fit Manfred hoards supplies and runs home. Fat Manfred heads home, gets tired, and joins
    a bunch of people hiding out in a homeless shelter.
    Fit Major Event: Build an armory
    Fat Major Event: Join the revolutionakies
4. Preparation: Fit Manfred builds an armory and starts training to be an assassin; he wants to be
    able to live like he used to. Fat Manfred joins the revolutionary group and preaches for peace
    and rebuilding.
    Fit Major Event: Assassinate the revolutionary leader
    Fat Major Event: Give a war-ending speech
5. End: There are four endings -
    1. Fit Manfred: kills Fat Manfred. Takes over the country with military force and becomes king.
    2. Fit Manfred: not fit enough to get past the soldiers. Gets locked up and eaten by rats.
    3. Fat Manfred: gets killed on his way to the park. The assassin slaughters all of Fat Manfred's
        friends and becomes king.
    4. Fat Manfred: succeeds in his speech. War ends and the world begins to rebuild. Due to his
        weight and health limitations, the revolutionaries had to take him in an armored truck, which
        saved him.


## Todos (Priority)

### Gameplay
* Change the stats to the new stats
* Add delays to the events (High)
* Add the instructional overlay (High)
* Add stat requirements to actions and action buttons (High)
* Change the action layout to fill the screen width and be scrollable (High)
* Add body changes after each major event (High)

### The Game Engine
* Add a level attribute to a save in the database
* Remove the counts from the save in the database and add counts for actions instead
* Update the Action#setActionLocked to consider level and stat_requirements
* Update Action#applyAction to use the new stat_changes attribute
* Add the rest of the actions (High)

### General Android (Probably won't do any of this)
* Check that all lifetime cycle stuff is handled (Medium)
* Add a home link to the Manfred screen (Low)
* Add action category fragment to action item layout (Low)

