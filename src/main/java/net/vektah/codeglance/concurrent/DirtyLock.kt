package net.vektah.codeglance.concurrent

class DirtyLock {
    public var locked = false
        private set

    public var dirty = false
        private set

    public fun acquire() : Boolean {
        synchronized(this) {
            if (locked) {
                // Someone else already grabbed the lock, we are dirty now
                dirty = true
                return false
            }

            locked = true
            return true
        }
    }

    public fun release() {
        synchronized(this) {
            locked = false
        }
    }

    public fun clean() {
        synchronized(this) {
            dirty = false
        }
    }
}