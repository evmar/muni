#!/usr/bin/python

from google.appengine.ext import db
from google.appengine.api.urlfetch import fetch
from muni import scrape
from django.utils import simplejson
import logging

class APIResult(db.Model):
    query = db.StringProperty(required=True)
    json = db.TextProperty(required=True)
    lastfetch = db.DateTimeProperty(auto_now_add=True)

def fetch_one(query):
    results = query.fetch(1)
    if len(results) > 0:
        return results[0]
    return None

def fetch_wireless_url(path):
    url = 'http://www.nextbus.com/wireless/' + path
    logging.info("Fetching %s..." % url)
    response = fetch(url, headers={'User-Agent': 'test'})
    if response.status_code != 200:
        raise "Bad status %d" % response.status_code
    return response.content

def get_routes():
    KEY = 'a=sf-muni'
    route = fetch_one(APIResult.all().filter('query =', KEY))
    if route:
        # TODO check date
        return route.json

    html = fetch_wireless_url('miniRoute.shtml?' + KEY)
    scraper = scrape.Wireless()
    routes = scraper.scrape_routes(html)

    json_data = [{'name': r.name, 'url': r.url} for r in routes]
    json = simplejson.dumps(json_data)
    result = APIResult(query=KEY, json=json)
    result.put()
    return result.json
