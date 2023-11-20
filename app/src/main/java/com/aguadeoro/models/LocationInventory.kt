package com.aguadeoro.models

data class LocationInventory(
    var detectedCodes: Set<String> = mutableSetOf(),
    var InventoryCodes: Set<String> = mutableSetOf(),
    var missingCodes: Set<String> = mutableSetOf(),
    var newCodes: Set<String> = mutableSetOf(),
): java.io.Serializable
