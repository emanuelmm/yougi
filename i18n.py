#!/usr/bin/env python
#-*- coding: utf-8 -*-
import os
import sys
import fnmatch
import argparse
import re
from xml.dom import minidom

try:
    import i18n_conf
except:
    print 'Error in i18n_conf file'
    sys.exit(1)


BASE_DIR = os.path.dirname(os.path.abspath(__file__))

def rglob(path, pattern):
    matches = []
    for root, dirnames, filenames in os.walk(path):
        for filename in fnmatch.filter(filenames, pattern):
            matches.append(os.path.join(root, filename))
    return matches


class Bundle(object):

    def __init__(self, filename, conf = None, debug=False):
        self.filename = filename
        self.locale = self._load_locale()
        self.keywords = self._load_keywords()
        self.conf = conf
        self.debug = debug

    def _load_locale(self):
        _, filename = os.path.split(self.filename)
        locale_re = re.match(r'Resources_(?P<locale>[^\.]+).properties', filename)
        if locale_re:
            return locale_re.group('locale')
        else:
            return 'default'

    def _load_keywords(self):
        keywords = {}
        with open(self.filename) as f:
            for line in f:
                if not line.startswith("#") and "=" in line:
                    key, value = line.split("=", 1)
                    keywords[key] = value
        return keywords

    def add(self, new_keyword):
        msg = self.conf['text_translate'].strip()
        msg = msg.format(key=new_keyword)
        self.keywords[new_keyword] = msg + '\n'

    def save(self):
        with open(self.filename, 'w') as f:
            if self.conf['text_base']:
                f.write(self.conf['text_base'])
            if self.debug:
                print "Saving keywords in: {fn}".format(fn=self.filename)
            conf_language = self.keywords.pop('confLanguage')
            f.write("confLanguage={}\n\n".format(conf_language))
            for key in sorted(self.keywords.keys()):
                f.write("{key}={value}\n".format(key=key, value=self.keywords[key].rstrip('\n')))
            self.keywords['confLanguage'] = conf_language


class I18NExtractor(object):

    def __init__(self, conf, debug=False):
        self.conf = conf
        self.var_name = conf['var_name']
        self.src_dir = conf['src_dir']
        self.i18n_dir = conf['i18n_dir']
        self.debug = debug
        self.src_keywords = []
        self.bundles = []
        self.new_keywords = []
        self.load_bundles()
        self.load_src_keywords()
        self.load_new_keywords()

    def load_bundles(self):
        for filename in rglob(self.i18n_dir, 'Resources*.properties'):
            self.bundles.append(Bundle(filename, conf=self.conf, debug=self.debug))

    def load_src_keywords(self):
        for filename in rglob(self.src_dir, '*.xhtml'):
            if self.debug:
                print "Extracting: {fn}".format(fn=filename)
            with open(filename) as f:
                text = f.read()
                self.src_keywords += re.findall(r'{v}\.(\w+)'.format(v=self.var_name), text)
        self.src_keywords = list(set(self.src_keywords))

    def load_new_keywords(self):
        bundle_default = [b for b in self.bundles if b.locale == 'default'][0]
        new_keywords = set(self.src_keywords) - set(bundle_default.keywords.keys())
        self.new_keywords = list(new_keywords)

    def extract(self):
        for new_keyword in self.new_keywords:
            for bundle in self.bundles:
                bundle.add(new_keyword)

    def save(self):
        if not self.new_keywords:
            print "*** No new keyword was found"
        else:
            print "*** New keywords: {kw}".format(kw=', '.join(self.new_keywords))
            for bundle in self.bundles:
                bundle.save()
        print "*** Finished!"


def load_conf():
    text_base =  getattr(i18n_conf, 'TEXT_BASE', '')
    text_translate = getattr(i18n_conf, 'TEXT_TRANSLATE', '[TRANSLATE: "{key}"]')
    # get SRC_DIR
    src_dir =  getattr(i18n_conf, 'SRC_DIR', None)
    if not src_dir:
        print 'SRC_DIR not configured in i18n_conf.py'
        sys.exit(1)
    src_dir = os.path.join(BASE_DIR, *src_dir.split('/'))
    # Get VAR_NAME
    var_name =  getattr(i18n_conf, 'VAR_NAME', None)
    if not var_name:
        print 'VAR_NAME not configured in i18n_conf.py'
        sys.exit(1)
    # Get I18N_DIR
    i18n_dir =  getattr(i18n_conf, 'I18N_DIR', None)
    if not i18n_dir:
        print 'i18n_dir not configured in i18n_conf.py'
        sys.exit(1)
    i18n_dir = os.path.join(BASE_DIR, *i18n_dir.split('/'))
    return {
        'text_base': text_base,
        'text_translate': text_translate,
        'src_dir': src_dir,
        'i18n_dir': i18n_dir,
        'var_name': var_name,
    }


def main(debug):
    conf = load_conf()
    i18n = I18NExtractor(conf, debug)
    i18n.extract()
    i18n.save()

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Extract keywords from source to i18n')
    parser.add_argument('-d', '--debug', action='store_true', help="execute with debug mode")
    args = parser.parse_args()
    main(args.debug)
