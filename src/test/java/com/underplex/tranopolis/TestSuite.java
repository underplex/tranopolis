package com.underplex.tranopolis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
   TestCity.class,
   TestRoad.class,
   TestSimpleTraffic.class,
   TestTurningTraffic.class,
   TestXingFinder.class
})

public class TestSuite {
 
}
