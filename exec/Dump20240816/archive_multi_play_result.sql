-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: archive
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `multi_play_result`
--

DROP TABLE IF EXISTS `multi_play_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `multi_play_result` (
  `multi_result_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `is_draw` tinyint(1) DEFAULT '0',
  `loser_score` float DEFAULT NULL,
  `play_time` bigint NOT NULL,
  `status` tinyint(1) DEFAULT '0',
  `winner_score` float DEFAULT NULL,
  `loser_id` bigint DEFAULT NULL,
  `sheet_id` bigint DEFAULT NULL,
  `winner_id` bigint DEFAULT NULL,
  PRIMARY KEY (`multi_result_id`),
  KEY `FKpnylxkxhtluww8kb8t5ltc0i3` (`loser_id`),
  KEY `FK25hjumxhpmda1fjpexg0bqk9v` (`sheet_id`),
  KEY `FKbjqmefg8xov9viarhsseov9s1` (`winner_id`),
  CONSTRAINT `FK25hjumxhpmda1fjpexg0bqk9v` FOREIGN KEY (`sheet_id`) REFERENCES `sheet` (`sheet_id`),
  CONSTRAINT `FKbjqmefg8xov9viarhsseov9s1` FOREIGN KEY (`winner_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKpnylxkxhtluww8kb8t5ltc0i3` FOREIGN KEY (`loser_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `multi_play_result`
--

LOCK TABLES `multi_play_result` WRITE;
/*!40000 ALTER TABLE `multi_play_result` DISABLE KEYS */;
INSERT INTO `multi_play_result` VALUES (1,'2024-08-15 10:30:01.540420','2024-08-15 10:30:01.540420',0,NULL,0,0,NULL,NULL,29,NULL);
/*!40000 ALTER TABLE `multi_play_result` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-16  9:40:49
