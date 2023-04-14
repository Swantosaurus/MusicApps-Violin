package com.kobera.music.common.notes

enum class InnerTwelveToneInterpretation {
    C {
        override val isSharp = false
        override fun nextTone() = CSharp
        override fun previousTone() = B
    },
    CSharp {
        override val isSharp = true
        override fun nextTone() = D
        override fun previousTone() = C
    },
    D {
        override val isSharp = false
        override fun nextTone() = DSharp
        override fun previousTone() = CSharp
    },
    DSharp {
        override val isSharp = true
        override fun nextTone() = E
        override fun previousTone() = D
    },
    E {
        override val isSharp = false
        override fun nextTone() = F
        override fun previousTone() = DSharp
    },
    F {
        override val isSharp = false
        override fun nextTone() = FSharp
        override fun previousTone() = E
    },
    FSharp {
        override val isSharp = true
        override fun nextTone() = G
        override fun previousTone() = F
    },
    G {
        override val isSharp = false
        override fun nextTone() = GSharp
        override fun previousTone() = FSharp
    },
    GSharp {
        override val isSharp = true
        override fun nextTone() = A
        override fun previousTone() = G
    },
    A {
        override val isSharp = false
        override fun nextTone() = ASharp
        override fun previousTone() = GSharp
    },
    ASharp {
        override val isSharp = true
        override fun nextTone() = B
        override fun previousTone() = A
    },
    B {
        override val isSharp = false
        override fun nextTone() = C
        override fun previousTone() = A
    };

    fun move(by :Int): InnerTwelveToneInterpretation{
        when(true){
            (by == 0) -> {
                return this
            }
            (by > 0) -> {
                var res  = this
                for(i in 0 until  by){
                    res = res.nextTone()
                }
                return res
            }
            else -> {
                var res  = this
                for(i in by until 0){
                    res = res.previousTone()
                }
                return  res
            }
        }
    }

    abstract val isSharp : Boolean

    abstract fun nextTone(): InnerTwelveToneInterpretation

    abstract fun previousTone() : InnerTwelveToneInterpretation

    fun difference(other: InnerTwelveToneInterpretation): Int {
        return this.ordinal - other.ordinal
    }

    companion object {
        fun fromIndex(i : Int){
            assert(i in 0..11) {
                "TwelveToneNotes needs 12 notes!!"
            }

            when(i){
                0 -> C
                1 -> CSharp
                2 -> D
                3 -> DSharp
                4 -> E
                5 -> F
                6 -> FSharp
                7 -> G
                8 -> GSharp
                9 -> A
                10 -> ASharp
                11 -> B
            }
        }
    }
}