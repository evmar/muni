#!/usr/bin/python

import unittest

import scrape

class TestSimpleScrape(unittest.TestCase):
    def setUp(self):
        self.scraper = scrape.Simple()

    def testStatus(self):
        times = self.scraper.scrape_status(open('data-simple/status.html'))
        self.assertEqual([12, 29, 30], times)

    def testStops(self):
        stops = self.scraper.scrape_stops(open('data-simple/stops.html'))
        self.assertEqual(u'Inbound to Embarcadero Station', stops[0].name)
        self.assertEqual(u'Church St & Clipper St', stops[0].stops[10].name)
        self.assertEqual(25, len(stops[0].stops))
        self.assertEqual(23, len(stops[1].stops))

    def testRoutes(self):
        routes = self.scraper.scrape_routes(open('data-simple/routes.html'))
        self.assertEqual(87, len(routes))
        self.assertEqual("J-Church", routes[1].name)

class TestWirelessScrape(unittest.TestCase):
    def setUp(self):
        self.scraper = scrape.Wireless()

    def testRoutes(self):
        routes = self.scraper.scrape_routes(open('data-wireless/miniRoute.shtml?a=sf-muni'))
        self.assertEqual(84, len(routes))
        self.assertEqual("F-Market & Wharves", routes[0].name)
        self.assertEqual("miniDirection.shtml?a=sf-muni&r=J", routes[1].url)
        self.assertEqual("California Street Cable Car", routes[-1].name)

    def testRoute(self):
        dirs = self.scraper.scrape_direction(open('data-wireless/miniDirection.shtml?a=sf-muni&r=J'))
        self.assertEqual(2, len(dirs))
        self.assertEqual('Inbound to Embarcadero Station', dirs[0].name)
        self.assertEqual('miniStop.shtml?a=sf-muni&r=J&d=J__IB2', dirs[0].url)

    def testStops(self):
        stops = self.scraper.scrape_stops(open('data-wireless/miniStop.shtml?a=sf-muni&r=J&d=J__IB2'))
        self.assertEqual(25, len(stops))

    def testStopEmpty(self):
        times = self.scraper.scrape_stop(open('data-wireless/miniPrediction.shtml?a=sf-muni&r=J&d=J__IB2&s=6217'))
        self.assertEqual(0, len(times))

    def testStop(self):
        times = self.scraper.scrape_stop(open('data-wireless/miniPrediction.shtml?a=sf-muni&r=N&d=N__IB3&s=5419'))
        self.assertEqual([1,6,25], times)

if __name__ == '__main__':
    unittest.main()
