#!/usr/bin/python

from django.utils import simplejson

data = {
  "direction": "Inbound",
  "stop": "21st and Dolores",
  "times": [2, 4, 13],
}

print 'Content-type: text/json'
print
print simplejson.dumps(data)
