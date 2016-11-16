package com.github.yu000hong.spring.common.common

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes = ['major', 'minor', 'revision'])
class Version implements Comparable {
    int major
    int minor
    int revision
    String raw

    public static Version from(String version) {
        if (!version) return null

        try {
            def list = [0, 0, 0] as int[]
            version.split('\\.').eachWithIndex { String entry, int i ->
                list[i] = entry.toInteger()
            }
            return new Version(major: list[0],
                    minor: list[1],
                    revision: list[2],
                    raw: version)
        } catch (ignored) {
            throw new IllegalArgumentException("invalid version: $version")
        }
    }

    @Override
    public String toString() {
        return "${major}.${minor}.${revision}"
    }

    @Override
    public int compareTo(other) {
        if (other instanceof String) {
            other = from(other)
        }
        if (major <=> other.major){
            return major <=> other.major
        }else if (minor <=> other.minor){
            return minor <=> other.minor
        }else{
            return revision <=> other.revision
        }
    }

}
