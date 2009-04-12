#!/usr/bin/python

from BeautifulSoup import BeautifulSoup
import re

def scrape_status(file):
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

class StopTable(object):
    def __init__(self, name=None, stops=None):
        self.name = name
        self.stops = stops

class Stop(object):
    def __init__(self, name, url):
        self.name = name
        self.url = url

def scrape_stops(file):
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


class Route(object):
    def __init__(self, name, url):
        self.name = name
        self.url = url


def scrape_routes(file):
    soup = BeautifulSoup(file, convertEntities=BeautifulSoup.ALL_ENTITIES)

    routes = []
    for route in soup.findAll('a', href=re.compile(r'simpleStopSelector')):
        url = route.href
        name = route.findNext(text=re.compile(r'\w+')).string
        routes.append(Route(name, url))
    return routes
