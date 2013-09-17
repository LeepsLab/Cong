function getMin() {
    return 0;
}

function getMax() {
    return 10;
}

function getNumStrategies() {
    return 2;
}

function getSubperiodBonus(subperiod, config) {
    return 0;
}

function getPayoff(id, percent, popStrategies, matchPopStrategies, config) {
    var total = 0;
    for (key in popStrategies) {
        total += popStrategies[key][0];
    }
    var mean = total / popStrategies.size();
    return 100 * mean;
}

function getPopStrategySummary(id, percent, popStrategies, matchPopStrategies) {
    return null;
}

function getMatchStrategySummary(id, percent, popStrategies, matchPopStrategies) {
    return null;
}

function configure() {
}