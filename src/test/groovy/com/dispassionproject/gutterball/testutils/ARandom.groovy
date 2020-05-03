package com.dispassionproject.gutterball.testutils

import com.github.javafaker.Faker

class ARandom {

    Faker faker = new Faker()

    def playerName() {
        faker.witcher().character()
    }

}
