#!/usr/bin/python

from BeautifulSoup import BeautifulSoup
import re

class StopTable(object):
    def __init__(self, name=None, stops=None):
        self.name = name
        self.stops = stops

class NamedURL(object):
    def __init__(self, name, url):
        self.name = name
        self.url = url

class Route(NamedURL):
    pass

class Direction(NamedURL):
    pass

class Stop(NamedURL):
    pass

class Simple:
    def scrape_status(self, file):
        soup = BeautifulSoup(file, convertEntities=BeautifulSoup.ALL_ENTITIES)

        # Skip to the beginning of the interesting content.
        node = soup.find(text=re.compile(r'Next\s+vehicles\s+for\s+line'))

        times = []
        extract_time = re.compile(r'(\d+)')

        # Extract all the times from the page.
        for n in node.findAllNext('div'):
            text = n.contents[0].string
            if text is None:
                break
            match = extract_time.search(text)
            if match is None:
                break
            times.append(int(match.group(1)))

        return times

    def scrape_stops(self, file):
        soup = BeautifulSoup(file, convertEntities=BeautifulSoup.ALL_ENTITIES)

        node = soup.find(text=re.compile(r'Direction:'))

        dir_names = []
        for direction in node.findAllNext('b'):
            dir_names.append(direction.string.strip())

        dir_stops = []
        tables = node.findAllNext('table')
        for table in tables:
            stops = []
            for a in table.findAll('a'):
                stops.append(Stop(name=a.findNext(text=re.compile(r'\w+')).string,
                                  url=a['href']))
            dir_stops.append(stops)

        assert len(dir_names) == 2
        assert len(dir_stops) == 2
        return (StopTable(dir_names[0], dir_stops[0]),
                StopTable(dir_names[1], dir_stops[1]))


    def scrape_routes(self, file):
        soup = BeautifulSoup(file, convertEntities=BeautifulSoup.ALL_ENTITIES)

        routes = []
        for route in soup.findAll('a', href=re.compile(r'simpleStopSelector')):
            url = route.href
            name = route.findNext(text=re.compile(r'\w+')).string
            routes.append(Route(name, url))
        return routes

class Wireless:
    def _scrape_list(self, file, target):
        soup = BeautifulSoup(file, convertEntities=BeautifulSoup.ALL_ENTITIES)

        for entry in soup.findAll('a', href=target):
            url = entry['href']
            name = entry.findNext(text=re.compile(r'\w+')).string
            yield (name, url)

    def scrape_routes(self, file):
        return [Route(name, url) for (name, url)
                in self._scrape_list(file, re.compile(r'miniDirection.shtml'))]

    def scrape_directions(self, file):
        return [Direction(name, url) for (name, url)
                in self._scrape_list(file, re.compile(r'miniStop.shtml'))]

    def scrape_stops(self, file):
        return [Stop(name, url) for (name, url)
                in self._scrape_list(file, re.compile(r'miniPrediction.shtml'))]

    def scrape_stop(self, file):
        soup = BeautifulSoup(file, convertEntities=BeautifulSoup.ALL_ENTITIES)

        times = []

        # Skip to the beginning of the interesting content.
        node = soup.find(text=re.compile(r'Next vehicles in'))

        if not node:
            # No next times?
            return times

        extract_time = re.compile(r'(\d+)')

        # Extract all the times from the page.
        for n in node.findAllNext('div'):
            text = n.contents[0].string
            if text is None:
                break
            match = extract_time.search(text)
            if match is None:
                break
            times.append(int(match.group(1)))

        return times

