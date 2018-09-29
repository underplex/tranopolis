# tranopolis

## Introduction
The tranopolis library provides tools for simulating traffic, with 2 distinctive features:

1. The simulation takes place on a simple grid map that lends itself to games or fun visualizations.

2. Individual drivers are modeled as agents each with its own defined driving behavior, so economic activity can be modeled.

## Your First Simulation

Look at the `Demo` class for an example project.

To simulate a city's traffic, the following steps are recommended:

1.	Instantiate a city.
2.	Make individual Lots within the City 'paved'. 
	'Paved' lots will become roads and intersections.
3.	Make individual Lots within the City 'built'.
	These can be grouped into Locations.
4.	Designate Locations by indicating groups of built Lots.
5.	Choose where each Location connects to the existing network of roads.
6.	Advance the simulation with the `city.advance()` method.

## The Three Layers

It might be helpful to think of the tranopolis city simulation in terms of "layers" of objects which a single City object will manipulate to simulate
traffic.

### Layer 1: Physical Plant (Lots)
A single `City` instance comprises a rectangular grid of Lots. A `Lot` can be undeveloped, paved, or built. You can think of this as the physical plant of the city.

```
City city = new City(5, 5);  
city.getLot(4, 1).makePaved();  
city.getLot(1, 4).makePaved();  
city.getLot(0, 4).makeBuilt();  
city.getLot(4, 0).makeBuilt();  
```

Like this, we can build a whole city. After some more paving and building, we can produce a quick console map with `city.getLotManager.printMap()`.
The city in the Demo class looks like this:  
```
b R . b .  
. R . R b  
. R . . .  
R R R R R  
b R . . b  
```
Here, R is a paved lot, b is a built lot, and the dot (aka period) is an undeveloped lot.

### Layer 2: Transportation (Locations, Roads, and Xings)
A place where you can work or play in this city is represented by a `Location`. Locations are defined by grouping built lots together.

`city.getLocationManager().makeLocation(city.getLot(4, 0), "Southern Hills Condos");`

Wait, but how do we refer to this location in the future? That same `makeLocation` method actually returns a reference we can use.

```
Location swMountainApts = city.getLocationManager().makeLocation(city.getLot(0, 0), "Southwest Mountain Apartments");  
Location nwHghtsOfficePark = city.getLocationManager().makeLocation(city.getLot(0, 4), "Northwest Heights Office Park");  
```
These Locations exist, but you have not yet established how they connect to the paved `Lot`s that will make up the actual network of roads. We can fix that easily.

```
city.connectLocation(southernHillsCondos, city.getLot(4, 1));  
city.connectLocation(nwHgthsOfficePark, city.getLot(1, 4));  
```

We probably want to make some people to travel in this city. We can determine where they live and work:

`city.getResidentManager().addResidents(makeBasics(10,swMountainApts,nwHghtsOfficePark));`

The BasicResident happens to have a constructor that uses the 2nd and 3rd arguments to set the `Resident`'s home and work.

### Layer 3: Navigation (Drivables and Graphs)

Now we need to make sure the city has fully mapped itself out. That's a single method call:

`city.getGraphManager().updateGraph();`

Don't call this method until all Locations and Lots are determined.

## API Reference

Reference docs live in the docs folder.

## Tests

Tests live in the Test folder.

## License

GPL 2.0
