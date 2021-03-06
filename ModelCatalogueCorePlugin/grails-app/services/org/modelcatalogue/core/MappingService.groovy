package org.modelcatalogue.core


class MappingService {

    static transactional = true

    Mapping map(CatalogueElement source, CatalogueElement destination, String mapping) {
        if (!source || !source.id || !destination || !destination.id || !mapping) return null
        Mapping existing = Mapping.findBySourceAndDestination(source, destination)
        if (existing) {
            return existing
        }
        Mapping newOne = new Mapping(source: source, destination: destination, mapping: mapping)
        newOne.save()
        source.addToOutgoingMappings(newOne)
        destination.addToIncomingMappings(newOne)
        newOne
    }

    Mapping map(CatalogueElement source, CatalogueElement destination, Map mapping) {
        map(source, destination, createMappingFunctionFromMap(mapping))
    }


    Mapping unmap(CatalogueElement source, CatalogueElement destination) {
        Mapping old = Mapping.findBySourceAndDestination(source, destination)
        if (!old) return null
        source.removeFromOutgoingMappings(old)
        destination.removeFromIncomingMappings(old)
        old.delete()
        old
    }


    private static String createMappingFunctionFromMap(Map map) {
        "${map.collectEntries { key, value -> [key, "\"${value}\""] }.toMapString()}[x]"
    }


}
