package com.example.serge.newsstand.pagination

interface PaginatorState {
    fun getTransitionEvents(event: Event): List<Event>
    fun nextState(event: Event): PaginatorState
}

class EMPTY : PaginatorState {

    override fun nextState(event: Event): PaginatorState{
        return if (event is Event.LoadMoreEvent) {
            EMPTY_PROGRESS()
            //Show full progress
        } else throw RuntimeException()
    }

    override fun getTransitionEvents(event: Event): List<Event> {
        return listOf(Event.ShowFullProgressEvent(true))
    }
}

class EMPTY_PROGRESS : PaginatorState {

    override fun nextState(event: Event): PaginatorState {
        if (event is Event.ShowDataEvent) {
            return if (event.data.isEmpty()) {
                EMPTY_DATA()
                //Hide full progress -> Show empty view
            } else {
                DATA()
                //Hide full progress -> Show data
            }
        } else throw RuntimeException()
    }

    override fun getTransitionEvents(event: Event): List<Event> {
        if (event is Event.ShowDataEvent) {
            return if (event.data.isEmpty()) {
                listOf(Event.ShowFullProgressEvent(false), Event.ShowEmptyViewEvent(true))
                //Hide full progress -> Show empty view
            } else {
                listOf(Event.ShowFullProgressEvent(false), Event.ShowDataEvent(event.data))
                //Hide full progress -> Show data
            }
        } else throw RuntimeException()
    }
}

class DATA : PaginatorState {

    override fun nextState(event: Event): PaginatorState {
        return when(event) {
            is Event.LoadNewPageEvent -> PAGE_PROGRESS() // Show page progress
            is Event.RefreshEvent -> REFRESH()
            else -> throw RuntimeException()
        }
    }

    override fun getTransitionEvents(event: Event): List<Event> {
        return when(event) {
            is Event.LoadNewPageEvent -> listOf(Event.ShowPageProgressEvent(true)) // Show page progress
            is Event.RefreshEvent -> listOf()
            else -> throw RuntimeException()
        }
    }
}

class PAGE_PROGRESS : PaginatorState {

    override fun nextState(event: Event): PaginatorState {
        return when(event) {
            is Event.ShowDataEvent -> DATA() // Hide page progress -> Show data
            is Event.RefreshEvent -> REFRESH() //Hide page progress ->
            else -> throw RuntimeException()
        }
    }

    override fun getTransitionEvents(event: Event): List<Event> {
        return when(event) {
            is Event.ShowDataEvent -> listOf(Event.ShowPageProgressEvent(false), Event.ShowDataEvent(event.data)) // Hide page progress -> Show data
            is Event.RefreshEvent -> listOf(Event.ShowPageProgressEvent(false)) //Hide page progress ->
            else -> throw RuntimeException()
        }
    }
}

class EMPTY_DATA : PaginatorState {

    override fun nextState(event: Event): PaginatorState {
        return when(event) {
            is Event.RefreshEvent -> REFRESH() // Hide empty view -> Show full progress
            else -> throw RuntimeException()
        }
    }

    override fun getTransitionEvents(event: Event): List<Event> {
        return when(event) {
            is Event.RefreshEvent -> listOf(Event.ShowEmptyViewEvent(false), Event.ShowFullProgressEvent(true)) // Hide empty view -> Show full progress
            else -> throw RuntimeException()
        }
    }
}

class REFRESH : PaginatorState {

    override fun nextState(event: Event): PaginatorState {
        if (event is Event.ShowDataEvent) {
            return if (event.data.isEmpty()) {
                EMPTY_DATA()
                //Hide full progress -> Show empty view
            } else {
                DATA()
                //Hide full progress -> Show data
            }
        } else throw RuntimeException()
    }

    override fun getTransitionEvents(event: Event): List<Event> {
        if (event is Event.ShowDataEvent) {
            return if (event.data.isEmpty()) {
                listOf(Event.ShowFullProgressEvent(false), Event.ShowEmptyViewEvent(true))
                //Hide full progress -> Show empty view
            } else {
               listOf(Event.ShowFullProgressEvent(false), Event.ShowDataEvent(event.data))
                //Hide full progress -> Show data
            }
        } else throw RuntimeException()
    }
}