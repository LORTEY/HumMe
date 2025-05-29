from lyricsgenius import Genius
genius = None
def set_global_genius(token):
    try:
        global genius
        genius = Genius(token)
        return True
    except:
        return False

def get_lyrics(author, song_name):
        try:
            song = genius.search_song(song_name,author)
            if song is not None:
                return song.lyrics
            else:
                return "ERROR: SONG NOT FOUND BY GENIUS"
        except(TimeoutError):
            return "ERROR: REQUEST TIMED OUT"
