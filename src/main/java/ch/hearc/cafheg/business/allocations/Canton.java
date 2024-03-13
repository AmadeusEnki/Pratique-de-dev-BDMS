package ch.hearc.cafheg.business.allocations;

import java.util.stream.Stream;

public enum Canton {
  NE,
  BE,
  FR,
  GE,
  SH,
  VD,
  VS,
  TG,
  ZH,
  TI,
  SO,
  SZ,
  LU,
  JU,
  GR,
  GL,
  AR,
  AI,
  AG,
  BL,
  BS,
  OW,
  NW,
  UR;

  // TODO: Ajouter les autres cantons.. (il y en a 26 en tout) => Fait

  public static Canton fromValue(String value) {
    return Stream.of(Canton.values())
        .filter(c -> c.name().equals(value))
        .findAny()
        .orElse(null);
  }
}
