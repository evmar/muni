#!/usr/bin/python

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from django.utils import simplejson
import backend
import logging
import urllib

class MainPage(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.out.write("usage: http://muni-api.appspot.com/api/")

class APIQuery(webapp.RequestHandler):
    def get(self, query):
        query = urllib.unquote(query)
        # if statements are from most-specific query component to least,
        # so the tests can each only examine one query param.
        if '&s=' in query:
            json = backend.get_stop(query)
        elif '&d=' in query:
            json = backend.get_stops(query)
        elif '&r=' in query:
            json = backend.get_route(query)
        elif not query or 'a=' in query:
            json = backend.get_routes()
        else:
            raise "Unknown query"
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.out.write(json)


application = webapp.WSGIApplication(
    [('/', MainPage),
     ('/api/(.*)', APIQuery)],
    debug=True)

def main():
  run_wsgi_app(application)

if __name__ == "__main__":
  main()
