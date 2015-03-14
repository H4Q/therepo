# BSD licensed by Johan Frisk <johan.frisk@hiq.se>
# Your mileage may vary, see README.md for helpful instructions
import json
#from urllib import request
import sys
import requests
import pickle
import hashlib
import time
import unittest
from operator import itemgetter


class TestRequestClass(unittest.TestCase):

	def test_001(self):

		request = {
        "query": [
        
        {
        "code": "Tid",
        "selection": {
            "filter": "item",
            "values": ["2013","2014" ]
      	}
    	}
  		],"response": {
    		"format": "json"
  		}
		}

		req = ScbRequest()

		req.addVariable("Tid",["2013","2014"])

		print(req.getDictRepr())

		self.assertEqual(req.getDictRepr(),request)

	def test_002(self):

		request = {
        "query": [
        {
        "code": "Tilltalsnamn",
        "selection": {
            "filter": "item",
            "values": [
            "ErikM"
            ]
            }
        },
        {
        "code": "Tid",
        "selection": {
            "filter": "item",
            "values": [
            "2013"
        ]
      }
    }
  ],
  "response": {
    "format": "json"
  }
}

		req = ScbRequest()

		req.addVariable("Tid","2013")
		req.addVariable("Tilltalsnamn",["ErikM"])

		print("\n")
		print(request)
		print(req.getDictRepr())

		self.assertEqual(req.getDictRepr(),request)

class ScbRequest:

	def __init__(self):
		self._variables = {}

	def addVariable(self,code,values):

		if type(values) == list:
			self._variables[code] = values
		else:
			self._variables[code] = [values] 

	def getDictRepr(self):
		dict = {}

		queryList = []

		for k,v in self._variables.items():
			_dict = {}
			_dict["code"] = k
			_dict["selection"] = { "filter": "item", "values": v}
			queryList.append(_dict)

		
		queryList.sort(key=lambda user: user['code'],reverse=True)

		dict["query"] = queryList
		dict["response"] = { "format": "json" }
		return dict


class ScbDataBase:
    def __init__(self,jsonObj):
        self._dbid = jsonObj["dbid"]
        self._text = jsonObj["text"]
        self._jsonRep = jsonObj
    def __str__(self):
        return str(self._text)

class ScbDataBaseList:
    def __init__(self,listObj):
        self._list = []
        for obj in listObj:
            self._list.append(ScbDataBaseListObj(obj))
    def __str__(self):
        return "Table listing with: {} elements".format(len(self._list))

    def __len__(self):
        return len(self._list)

    def __getitem__(self,key):
        try:
            l = len(self._list[key])
        except TypeError:
            l = 0
        if l > 1:
            return ScbDataBaseList(self._list[key])
        else:
            return ScbDataBaseListObj(self._list[key])

class ScbDataBaseListObj:
    def __init__(self,dictObj):
        if type(dictObj) == dict:
            self._text = dictObj['text']
            self._id = dictObj['id']
            self._jsonRep = dictObj
        elif type(dictObj) == ScbDataBaseListObj:
            self._text = dictObj._text
            self._id = dictObj._id
            self._jsonRep = dictObj._jsonRep

    def __str__(self):
        return self._text

    def id(self):
        return self._id

class ScbDataBaseTable:
    def __init__(self,dictObj):
        self._title = dictObj['title']
        self._variables = list()
        for var in dictObj['variables']:
            self._variables.append(ScbDataBaseVariable(var))


    def __str__(self):
        return self._title

    def __len__(self):
        return len(self._variables)

    def __getitem__(self,key):
        return self._variables[key]

    def variables(self):
        return self._variables

class ScbDataBaseVariable:
    def __init__(self,dictRep):
        self._elimination =  dictRep['elimination'] if 'elimination' in dictRep else False
        self._text = dictRep['text']
        self._code = dictRep['code']
        self._values = dictRep['values']
        self._time = dictRep['time'] if 'time' in dictRep else False
        self._valueTexts = dictRep['valueTexts']

    def __str__(self):
        return self._code

class ScbDataFactory:
    def create(jsonObj):

        if len(jsonObj) == 1:
            jsonObj = jsonObj[0]

        if type(jsonObj) == list:
            return ScbDataBaseList(jsonObj)
        elif type(jsonObj) != dict:
            raise Exception("Wrong type")
        elif 'dbid' in jsonObj:
            return ScbDataBase(jsonObj)
        elif 'variables' in jsonObj:
            return ScbDataBaseTable(jsonObj)

def __save_obj(obj, name ):
    with open('obj/'+ name + '.pkl', 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)

def __load_obj(name ):
    with open('obj/' + name + '.pkl', 'rb') as f:
        return pickle.load(f)



def getTable(tableUrl):
    obj = _getRequest(tableUrl)

    req = ScbRequest()
    for v in obj.variables():
        req.addVariable(str(v),v._values)

    request = req.getDictRepr()
    

    response = getScbJsonData(tableUrl,request)
   
    return response

def _getRequest(url):
    sha1Sum = hashlib.sha1()
    sha1Sum.update(bytes(url,"utf-8"))
    shaHash = sha1Sum.hexdigest()

    try:
        jsonObj = __load_obj(shaHash)
    except FileNotFoundError:
        print("loading {} with new GET request...".format(url))
        #time.sleep(0.5)
        response = request.urlopen(url)
        strResponse= response.readall().decode('utf-8')
        jsonObj = json.loads(strResponse)

        __save_obj(jsonObj,shaHash)        

    return ScbDataFactory.create(jsonObj)

def getScbJsonData(url,postRequest):
    sha1Sum = hashlib.sha1()
    sha1Sum.update(bytes(url,"utf-8"))
    shaHash = sha1Sum.hexdigest()+"_data"

    print("\n\rloading table from: {} ...".format(url),end="")

    try:
        jsonObj = __load_obj(shaHash)
        print("Done!\r")
        print("File saved to: obj/{}.pkl".format(shaHash))
        return jsonObj
    except FileNotFoundError:
        response = requests.post(url,json=postRequest)
        __save_obj(response.json(),shaHash)
        print("Done!\r")
        print("File saved to: obj/{}.pkl".format(shaHash))
        return response.json()


class ScbData:

    def __init__(self,language="Swedish"):
        if language == "Swedish":
            self.language = 'sv'
        elif language == "English":
            self.language = 'en'
        else:
            raise Exception("Unknown language: {}".format(self.language))

        self.urlBase = 'http://api.scb.se/OV0104/v1/doris/'+self.language+'/ssd/'



    def getRequest(self,suffix=""):
        url = self.urlBase + str(suffix)
        return _getRequest(url)


if __name__=='__main__':
	unittest.main()
