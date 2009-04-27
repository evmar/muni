#!/usr/bin/python

import unittest

import scrape

class TestSimpleScrape(unittest.TestCase):
    def setUp(self):
        self.scraper = scrape.Simple()

    def testStatus(self):
        times = self.scraper.scrape_status(open('test/simple/status.html'))
        self.assertEqual([12, 29, 30], times)

    def testStops(self):
        stops = self.scraper.scrape_stops(open('test/simple/stops.html'))
        self.assertEqual(u'Inbound to Embarcadero Station', stops[0].name)
        self.assertEqual(u'Church St & Clipper St', stops[0].stops[10].name)
        self.assertEqual(25, len(stops[0].stops))
        self.assertEqual(23, len(stops[1].stops))

    def testRoutes(self):
        routes = self.scraper.scrape_routes(open('test/simple/routes.html'))
        self.assertEqual(87, len(routes))
        self.assertEqual("J-Church", routes[1].name)

class TestWirelessScrape(unittest.TestCase):
    def setUp(self):
        self.scraper = scrape.Wireless()

    def testRoutes(self):
        routes = self.scraper.scrape_routes(open('test/wireless/miniRoute.shtml?a=sf-muni'))
        self.assertEqual(84, len(routes))
        self.assertEqual("F-Market & Wharves", routes[0].name)
        self.assertEqual("miniDirection.shtml?a=sf-muni&r=J", routes[1].url)
        self.assertEqual("California Street Cable Car", routes[-1].name)

    def testRoute(self):
        dirs = self.scraper.scrape_directions(open('test/wireless/miniDirection.shtml?a=sf-muni&r=J'))
        self.assertEqual(2, len(dirs))
        self.assertEqual('Inbound to Embarcadero Station', dirs[0].name)
        self.assertEqual('miniStop.shtml?a=sf-muni&r=J&d=J__IB2', dirs[0].url)

    def testStops(self):
        stops = self.scraper.scrape_stops(open('test/wireless/miniStop.shtml?a=sf-muni&r=J&d=J__IB2'))
        self.assertEqual(25, len(stops))

    def testStopEmpty(self):
        times = self.scraper.scrape_stop(open('test/wireless/miniPrediction.shtml?a=sf-muni&r=J&d=J__IB2&s=6217'))
        self.assertEqual(0, len(times))

    def testStopEmpty2(self):
        times = self.scraper.scrape_stop(open('test/wireless/miniPrediction.shtml?a=sf-muni&r=J&d=J__IB2&s=3537&s=6217'))
        self.assertEqual(0, len(times))

    def testStop(self):
        times = self.scraper.scrape_stop(open('test/wireless/miniPrediction.shtml?a=sf-muni&r=N&d=N__IB3&s=5419'))
        self.assertEqual([1,6,25], times)

    def testStop2(self):
        times = self.scraper.scrape_stop(open('test/wireless/miniPrediction.shtml?a=sf-muni&r=N&d=N__IB3&s=5212'))
        self.assertEqual([2], times)

    def testStop3(self):
        times = self.scraper.scrape_stop(open('test/wireless/miniPrediction.shtml?a=sf-muni&r=3&d=03_IB1_M&s=5143'))
        self.assertEqual([13,33,53], times)

if __name__ == '__main__':
    unittest.main()
