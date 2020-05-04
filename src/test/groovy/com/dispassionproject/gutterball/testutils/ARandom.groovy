package com.dispassionproject.gutterball.testutils

import com.github.javafaker.Faker

class ARandom {

    Faker faker = new Faker()

    def playerName() {
        faker.witcher().character()
    }

    def playerNameSet(int count = 4) {
        def names = [] as Set<String>
        while (names.size() < count) {
            names << playerName()
        }
        names
    }

    def gameId() {
        UUID.fromString(faker.internet().uuid())
    }

    def playerId() {
        UUID.fromString(faker.internet().uuid())
    }

    def pins(int max = 10) {
        faker.number().numberBetween(0, max)
    }

    def frame() {
        def pins = pins()
        pins == 0 ? [10] : [pins, 10-pins]
    }

}
