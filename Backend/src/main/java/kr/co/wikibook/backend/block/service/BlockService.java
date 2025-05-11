package kr.co.wikibook.backend.block.service;

public interface BlockService {

    // 토큰 차단 데이터 삽입
    Integer addBlock(String token);

    // 토큰 차단 데이터가 있는지 확인
    boolean hasBlock(String token);
}
