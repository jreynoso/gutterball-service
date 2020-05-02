package com.dispassionproject.gutterball

import com.github.javafaker.Faker
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class GutterballApplicationSpec extends Specification {

    Faker faker = Faker.instance()

    def "should test a thing"() {
        given:
        def thing = faker.witcher().character()

        expect:
        thing
    }

}
