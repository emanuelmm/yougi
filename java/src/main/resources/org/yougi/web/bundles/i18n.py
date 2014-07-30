#!/usr/bin/env python
#-*- coding: utf-8 -*-
from glob import iglob
import re


def get_locale(filename):
    locale_re = re.match(r'Resources_(?P<locale>[^\.]+).properties', filename)
    if locale_re:
        return locale_re.group('locale')
    return 'default'


def load_keys(filename):
    bundle = {}
    locale = get_locale(filename)
    with open(filename) as f:
        for line in f:
            if not line.startswith("#") and "=" in line:
                key, value = line.split("=", 1)
                bundle[key] = value
    return locale, bundle


def load_files():
    locales = {}
    for filename in iglob('Resources*.properties'):
        locale, bundle = load_keys(filename)
        locales[locale] = bundle
    return locales


def create_key(locales, key):
    create = raw_input("The key '{0}' does not exist."
                       "Do you want to create it? (y, n): ".format(key))
    if create == 'y':
        for locale, bundle in locales.items():
            bundle[key] = raw_input("{0}: ".format(locale))


def update_key(locales, key):
    change = raw_input('Change ? (y, n): ')
    if change == 'y':
        for locale, bundle in locales.items():
            new_value = raw_input("%s: from '%s' to " % (locale, bundle[key].rstrip('\n')))
            if new_value:
                bundle[key] = new_value


def key_exists(locales, key):
    exists = False
    for locale, bundle in locales.items():
        if key in bundle:
            print '{0:7}: {1:8}'.format(locale, bundle[key].rstrip('\n'))
            exists = True
    return exists


def cmd_interface(locales):
    print "Leave the key empty to exit."
    key = raw_input('Search Key: ')
    if not key:
        return False
    if key_exists(locales, key):
        update_key(locales, key)
    else:
        create_key(locales, key)
    return True


def main():
    locales = load_files()
    while cmd_interface(locales):
        print '-' * 79

if __name__ == '__main__':
    main()
