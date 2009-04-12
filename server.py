#!/usr/bin/python

from django.utils import simplejson

data = {
  "direction": "Inbound to Embarcadero Station",
  "name": "21st and Dolores",
  "times": [2, 4, 13],
}

print 'Content-type: text/plain'
print
print simplejson.dumps(data)
