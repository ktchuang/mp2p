#    A validation script for Bonnmotion (http://net.cs.uni-bonn.de/wg/cs/applications/bonnmotion/)
#    Copyright (C) 2011 University of Bonn
#
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.

import ConfigParser

CONFIGFILENAME = 'validate.cfg'
#
# This class reads a config file and ...
# singleton..
#    @TODO error checking... (ex. are dbfiles valid...)
#    is BMPATH valid???
class Config(object):
    def __new__(type, *args):
        if not '_the_instance' in type.__dict__:
            type._the_instance = object.__new__(type)
        return type._the_instance
    
    def __init__(self):
        if not '_ready' in dir(self):
            self._ready = True
            self._configparser = ConfigParser.ConfigParser()
            self._configparser.read(CONFIGFILENAME)
            
    def setArgsConfig(self, argsconfig):
        self.argsconfig = argsconfig
    
    def readConfigEntry(self, entryname):
        if self.argsconfig is not None:
            if self.argsconfig.has_key(entryname):
                return argsconfig[entryname]
            
        try:
            return int(self._configparser.get('validate', entryname))
        except ValueError:
            return self._configparser.get('validate', entryname)