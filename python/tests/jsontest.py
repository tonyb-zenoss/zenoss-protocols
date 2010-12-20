###########################################################################
#
# This program is part of Zenoss Core, an open source monitoring platform.
# Copyright (C) 2010, Zenoss Inc.
#
# This program is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License version 2 as published by
# the Free Software Foundation.
#
# For complete information please visit: http://www.zenoss.com/oss/
#
###########################################################################

import unittest
from uuid import uuid4
from json import dumps, loads

from time import time
import pkg_resources # Import this so zenoss.protocols will be found
from zenoss.protocols.jsonformat import to_dict, to_json, from_dict, from_json
from protobufs.test_pb2 import TestMessage, OPTION_A, OPTION_B, OPTION_C

class JsonTest(unittest.TestCase):
    data = {
        'uuid' : str(uuid4()),
        'created_time' : int(time() * 1000),
        'test_enum' : OPTION_B,
        'messages' : [
            {
                'uuid' : str(uuid4()),
                'test_enum' : OPTION_A,
            },
            {
                'uuid' : str(uuid4()),
                'test_enum' : OPTION_C,
            },
        ],
        'nested' : {
            'uuid' : str(uuid4()),
            'test_enum' : OPTION_A,
        }
    }

    def _compareProtoDict(self, data, pb):
        assert data['uuid'] == pb.uuid
        assert data['created_time'] == pb.created_time
        assert data['test_enum'] == pb.test_enum
        assert data['nested']['uuid'] == pb.nested.uuid
        assert data['nested']['test_enum'] == pb.nested.test_enum
        assert data['messages'][0]['uuid'] == pb.messages[0].uuid
        assert data['messages'][0]['test_enum'] == pb.messages[0].test_enum
        assert data['messages'][1]['uuid'] == pb.messages[1].uuid
        assert data['messages'][1]['test_enum'] == pb.messages[1].test_enum

    def setUp(self):
        self.message = TestMessage()
        self.message.uuid = self.data['uuid']
        self.message.created_time = self.data['created_time']
        self.message.test_enum = self.data['test_enum']
        self.message.nested.uuid = self.data['nested']['uuid']
        self.message.nested.test_enum = self.data['nested']['test_enum']

        m1 = self.message.messages.add()
        m1.uuid = self.data['messages'][0]['uuid']
        m1.test_enum = self.data['messages'][0]['test_enum']

        m2 = self.message.messages.add()
        m2.uuid = self.data['messages'][1]['uuid']
        m2.test_enum = self.data['messages'][1]['test_enum']

    def testToJson(self):
        json = dumps(self.data)
        jsonified = to_json(self.message)

        # Can't test json strings, need to turn it back to dicts
        assert(loads(json) == loads(jsonified))

    def testToDict(self):
        data = to_dict(self.message)
        self._compareProtoDict(data, self.message)

    def testFromDict(self):
        pb = from_dict(TestMessage, self.data)
        self._compareProtoDict(self.data, pb)

    def testFromJson(self):
        json = dumps(self.data)
        pb = from_json(TestMessage, json)
        self._compareProtoDict(self.data, pb)

if __name__ == '__main__':
    unittest.main()