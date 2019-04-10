# IDIoT

IDIoT is an acronym for "IDIoT Distrusts the Internet of Things"

It's a MQTT subscriber to accept messages from your sensors and use
them to

* update a model of what your home is doing
* perform actions when things happen

# How it works

A signal is a series of timestamped values that come from a sensor.

A signal processor is a function of one or more signals which computes
a new signal.

Signal processors can act on the signals output from other processors,
as well as/instead of on raw signals.

There are no loops.  I don't have an actual reason that loops would
break it, I just don't want to have to think about whether they might.

Signals are stored in a time-series database (or at least, some kind
of datastore that looks a bit like one) so that processors can
calculate based on historical values,not just current values.  There
is a cutoff time after which old values are discarded, but I have no
idea yet how that's set.  There may be a need to keep some kinds of
events for longer than others - e.g. security camera on/off times may
be worth hanging onto for longer than indoor thermostat readings.

The outputs from the signal processing graph may be used to send new
MQTT messages to actuators ("turn on the boiler CH heat demand relay")
or to perform other arbitrary side effects ("email the property owner").

Dont yet know how to evaluate the graph as often as needed but no more
frequently than necessary.  It should be tied into new events coming
in, somehow: each node should only be recalculated when its input
changes (which means we need to be able to tell what its inputs are).
Or maybe it's OK to say that recalculation is allowed to happen
whenever - I mean, it's pure computation and so probably quite fast,
right? - but side-effects should only happen when outputs change.


do we have to keep the value of each node in the graph?  maybe we can
store it in the time-series db

