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

if __name__ == '__main__':
    unittest.main()
