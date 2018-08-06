import org.k3v1n.bowling.Game
import org.k3v1n.bowling.GameEndException
import spock.lang.Specification
import spock.lang.Unroll

class BowlingSpec extends Specification {
    def "a game consists of 10 frames"() {
        given: "a new game"
        def game = new Game()

        when: "the number of Frames is retrieved"
        def frameCount = game.Frames().length

        then: "it should be 10"
        frameCount == 10
    }

    def "a new game should have an initial total score of 0"() {
        given:
        def game = new Game()

        when:
        def totalScore = game.TotalScore()

        then:
        totalScore == 0
    }

    def "when a new game is started all 10 frames should be initialized with zero score"() {
        given: "a new game"
        def game = new Game()

        when: "the Frames are retrieved"
        def frames = game.Frames()

        then: "all Frames should have a initial total score of 0"
        frames[0].Score == 0
        frames[1].Score == 0
        frames[2].Score == 0
        frames[3].Score == 0
        frames[4].Score == 0
        frames[5].Score == 0
        frames[6].Score == 0
        frames[7].Score == 0
        frames[8].Score == 0
        frames[9].Score == 0
    }

    def "when a new game is started it should not be over"() {
        given:
        def game = new Game()

        when:
        def over = game.Over()

        then:
        over == false
    }

    @Unroll
    def "frame pins below 10 roll 1 hit #pinsRoll1 and roll 2 hit #pinsRoll2 the frame score should be #expectedScore"(
            int pinsRoll1, int pinsRoll2, int expectedScore) {
        given:
        def game = new Game()

        when:
        game.AddRoll(pinsRoll1)
        game.AddRoll(pinsRoll2)

        then:
        game.Frames()[0].Score == expectedScore
        game.TotalScore() == expectedScore

        where:
        pinsRoll1 | pinsRoll2 | expectedScore
        0         | 0         | 0
        2         | 2         | 4
        1         | 2         | 3
        0         | 5         | 5
        0         | 9         | 9
    }

    def "on a strike the current frame gets a score of 10 plus the score of the next frame"() {
        given:
        def game = new Game()

        when:
        game.AddRoll(10)
        game.AddRoll(2)
        game.AddRoll(5)

        then:
        game.Frames()[0].Score == 17
        game.Frames()[1].Score == 7
        game.TotalScore() == 24
    }

    def "on a spare the current frame gets a score of 10 plus the score of the first roll in the next frame"() {
        given:
        def game = new Game()

        when:
        game.AddRoll(3)
        game.AddRoll(7)
        game.AddRoll(8)
        game.AddRoll(0)

        then:
        game.Frames()[0].Score == 18
        game.Frames()[1].Score == 8
        game.TotalScore() == 26
    }

    def "a normal game ends after the tenth frame"() {
        given:
        def game = new Game()

        when:
        game.AddRoll(1)
        game.AddRoll(4)

        game.AddRoll(4)
        game.AddRoll(5)

        game.AddRoll(6)
        game.AddRoll(4)

        game.AddRoll(5)
        game.AddRoll(5)

        game.AddRoll(10)

        game.AddRoll(0)
        game.AddRoll(1)

        game.AddRoll(7)
        game.AddRoll(3)

        game.AddRoll(6)
        game.AddRoll(4)

        game.AddRoll(10)

        game.AddRoll(2)
        game.AddRoll(0)

        then:

        game.TotalScore() == 111
        game.Over() == true
    }

    def "a finished game should throw an exception when a new roll is done"() {
        given:
        def game = new Game()
        game.AddRoll(1)
        game.AddRoll(4)

        game.AddRoll(4)
        game.AddRoll(5)

        game.AddRoll(6)
        game.AddRoll(4)

        game.AddRoll(5)
        game.AddRoll(5)

        game.AddRoll(10)

        game.AddRoll(0)
        game.AddRoll(1)

        game.AddRoll(7)
        game.AddRoll(3)

        game.AddRoll(6)
        game.AddRoll(4)

        game.AddRoll(10)

        game.AddRoll(2)
        game.AddRoll(0)

        when:
        game.AddRoll(10)

        then:
        thrown(GameEndException)
    }

    def "when the tenth frame is a spare then a third roll is available"() {
        given:
        def game = new Game()

        game.AddRoll(1)
        game.AddRoll(4)

        game.AddRoll(4)
        game.AddRoll(5)

        game.AddRoll(6)
        game.AddRoll(4)

        game.AddRoll(5)
        game.AddRoll(5)

        game.AddRoll(10)

        game.AddRoll(0)
        game.AddRoll(1)

        game.AddRoll(7)
        game.AddRoll(3)

        game.AddRoll(6)
        game.AddRoll(4)

        game.AddRoll(10)

        when:
        game.AddRoll(2)
        game.AddRoll(8)

        then:
        game.AddRoll(6)
        game.TotalScore() == 133
    }

    def "a frame should be scored immediately"() {
        given:
        def game = new Game()

        when:
        game.AddRoll(1)
        game.AddRoll(4)

        then:
        game.Frames()[0].Score == 5

        and:
        game.AddRoll(4)
        game.AddRoll(5)

        then:
        game.Frames()[1].Score == 9

        and:
        game.AddRoll(6)
        game.AddRoll(4)

        then:
        game.Frames()[2].Score == 10

        and:
        game.AddRoll(5)
        game.AddRoll(5)

        then:
        game.Frames()[2].Score == 15
        game.Frames()[3].Score == 10

        and:
        game.AddRoll(10)

        then:
        game.Frames()[3].Score == 20
        game.Frames()[4].Score == 10

        and:
        game.AddRoll(0)
        game.AddRoll(1)

        then:
        game.Frames()[4].Score == 11
        game.Frames()[5].Score == 1

        and:
        game.AddRoll(7)
        game.AddRoll(3)

        then:
        game.Frames()[6].Score == 10

        and:
        game.AddRoll(6)
        game.AddRoll(4)

        then:
        game.Frames()[6].Score == 16
        game.Frames()[7].Score == 10

        and:
        game.AddRoll(10)

        then:
        game.Frames()[7].Score == 20
        game.Frames()[8].Score == 10

        and:
        game.AddRoll(2)
        game.AddRoll(8)
        game.AddRoll(6)

        then:
        game.Frames()[8].Score == 20
        game.Frames()[9].Score == 16
    }

    def "a game with all strikes scores 300"() {
        given:
        def game = new Game()

        when:
        game.AddRoll(10)

        game.AddRoll(10)

        game.AddRoll(10)

        game.AddRoll(10)

        game.AddRoll(10)

        game.AddRoll(10)

        game.AddRoll(10)

        game.AddRoll(10)

        game.AddRoll(10)

        game.AddRoll(10)

        game.AddRoll(10)

        game.AddRoll(10)

        then:
        game.TotalScore() == 300
    }
}
