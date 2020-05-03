package com.dispassionproject.gutterball

import com.dispassionproject.gutterball.controller.GameController
import org.springframework.beans.factory.annotation.Autowired

class GutterballApplicationSpec extends BaseIntSpec {

    @Autowired
    GameController gameController

    def "should load application context"() {
        expect:
        gameController
    }

}
