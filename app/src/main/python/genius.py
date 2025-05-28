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
        return song.lyrics
    except:
        try:
            artist = genius.search_artist(author)
            for song in artist.songs:
                if song.title == song_name:
                    return song.lyrics
        except:
            return ""
