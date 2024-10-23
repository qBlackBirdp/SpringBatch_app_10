package com.hys.exam.springbatch_app_10.app.song.repository;

import com.hys.exam.springbatch_app_10.app.song.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
}