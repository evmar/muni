#!/usr/bin/python

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from django.utils import simplejson
import backend

class MainPage(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        data = {
            "direction": "Inbound to Embarcadero Station",
            "name": "21st and Dolores",
            "times": [2, 4, 13],
            }
        self.response.out.write(simplejson.dumps(data))

class APIQuery(webapp.RequestHandler):
    def get(self, query):
        self.response.headers['Content-Type'] = 'text/plain'
        json = backend.get_routes()
        self.response.out.write(json)


application = webapp.WSGIApplication(
                                     [('/', MainPage),
                                      ('/api/(.*)', APIQuery)],
                                     debug=True)

def main():
  run_wsgi_app(application)

if __name__ == "__main__":
  main()
