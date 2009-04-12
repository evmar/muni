#!/usr/bin/python

import unittest

import scrape

class TestScrape(unittest.TestCase):
    def testStatus(self):
        times = scrape.scrape_status(open('data/status.html'))
        self.assertEqual([12, 29, 30], times)

    def testStops(self):
        stops = scrape.scrape_stops(open('data/stops.html'))
        self.assertEqual(u'Inbound to Embarcadero Station', stops[0].name)
        self.assertEqual(u'Church St & Clipper St', stops[0].stops[10].name)
        self.assertEqual(25, len(stops[0].stops))
        self.assertEqual(23, len(stops[1].stops))

if __name__ == '__main__':
    unittest.main()
