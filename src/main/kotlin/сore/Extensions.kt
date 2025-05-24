package сore

import Data.*
import java.util.*

fun Hashtable<String, Worker>.filterByStatus(status: Status?): Map<String, Worker> {
    return if (status != null) {
        filterValues { it.Status == status }
    } else {
        filterValues { it.Status == null }
    }
}

fun Hashtable<String, Worker>.getWorkersSortedBySalary(): List<Worker> {
    return values.sortedByDescending { it.Salary }
}

fun Hashtable<String, Worker>.getPositionsFrequency(): Map<Position, Int> {
    return values.groupingBy { it.Position }.eachCount()
}