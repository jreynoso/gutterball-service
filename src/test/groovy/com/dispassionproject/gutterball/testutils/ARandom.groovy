package com.dispassionproject.gutterball.testutils

import com.github.javafaker.Faker

class ARandom {

    Faker faker = new Faker()

    def player() {
        faker.witcher().character()
    }

}
