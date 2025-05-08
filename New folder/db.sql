-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: communityapp
-- ------------------------------------------------------
-- Server version	8.0.41

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
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `category_id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) NOT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_name` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (7,'Baby & Kids Essentials'),(3,'Books & Stationery'),(4,'Clothing & Fashion'),(1,'Electronics & Gadgets'),(2,'Furniture & Home Essentials'),(5,'Home-Cooked Food & Groceries'),(6,'Medicines & Health Products'),(9,'Musical instruments'),(8,'Sports & Fitness');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `donation_item_images`
--

DROP TABLE IF EXISTS `donation_item_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `donation_item_images` (
  `image_id` bigint NOT NULL AUTO_INCREMENT,
  `image_path` text NOT NULL,
  `donation_id` char(36) DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `FK_donation_images_donation` (`donation_id`),
  CONSTRAINT `FK_donation_images_donation` FOREIGN KEY (`donation_id`) REFERENCES `donation_items` (`donation_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `donation_item_images`
--

LOCK TABLES `donation_item_images` WRITE;
/*!40000 ALTER TABLE `donation_item_images` DISABLE KEYS */;
INSERT INTO `donation_item_images` VALUES (2,'Donations/8bd6e7ac-2a7d-45c7-8e5a-8317fd0208e4_1000000047.jpg','93bf71a7-3ee9-4e6e-9a3c-596c9ea9a686'),(3,'Donations/87785926-5a88-48e9-a6cd-3ecfcb55be5c_1000000048.jpg','93bf71a7-3ee9-4e6e-9a3c-596c9ea9a686'),(4,'Donations/149ce4af-c882-480d-80d8-fce1fd146816_1000000049.jpg','93bf71a7-3ee9-4e6e-9a3c-596c9ea9a686'),(5,'Donations/6f5ce8d4-944c-4133-bb96-68ab472dcaf2_1000000045.jpg','93bf71a7-3ee9-4e6e-9a3c-596c9ea9a686'),(7,'Donations/c42f825b-f32a-4d58-b647-dcf9400d27dd_1000000046.jpg','93bf71a7-3ee9-4e6e-9a3c-596c9ea9a686'),(8,'Donations/d1de2ee5-9025-4a6f-9f31-a60fd88b233e_download (18).jpg','b030170e-1438-491b-93e5-69d813feec1e'),(9,'Donations/6c38bd83-8420-4454-b139-feeb44c4b272_download (19).jpg','b030170e-1438-491b-93e5-69d813feec1e'),(10,'Donations/e243940b-0daf-45b2-8c8c-d64aa05ed5a9_download (20).jpg','b030170e-1438-491b-93e5-69d813feec1e'),(11,'Donations/6e233b83-32a5-4e32-84b4-a3052a0cceb6_download (21).jpg','b030170e-1438-491b-93e5-69d813feec1e'),(12,'Donations/ea233dfe-e888-416c-95c8-0053b9057acb_download (22).jpg','b030170e-1438-491b-93e5-69d813feec1e'),(13,'Donations/bbc9bb15-b0e8-4625-9c59-21d4c5341d4b_download (7).jpg','d0610874-0301-4187-b24d-c8cbf02c0d96'),(14,'Donations/6f558841-18fd-499a-baba-969663627b86_download (3).jpg','d0610874-0301-4187-b24d-c8cbf02c0d96'),(15,'Donations/8c2eb505-2c6f-483d-abfc-cd9e0ea699e5_download (4).jpg','d0610874-0301-4187-b24d-c8cbf02c0d96'),(16,'Donations/c3a87156-21ed-46b4-81b1-d730545b3804_download (6).jpg','d0610874-0301-4187-b24d-c8cbf02c0d96'),(17,'Donations/22e92b50-b744-4aec-a924-eae650b92da1_download (5).jpg','d0610874-0301-4187-b24d-c8cbf02c0d96');
/*!40000 ALTER TABLE `donation_item_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `donation_items`
--

DROP TABLE IF EXISTS `donation_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `donation_items` (
  `donation_id` char(36) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text,
  `owner_id` char(36) NOT NULL,
  `status` varchar(50) DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`donation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `donation_items`
--

LOCK TABLES `donation_items` WRITE;
/*!40000 ALTER TABLE `donation_items` DISABLE KEYS */;
INSERT INTO `donation_items` VALUES ('93bf71a7-3ee9-4e6e-9a3c-596c9ea9a686','test update 2','test description update 2','2929166e-473e-43ba-8b51-ced6ed6dc42f','RESERVED','2025-04-16 01:34:29'),('b030170e-1438-491b-93e5-69d813feec1e','test user donation','test user donation description','14661d75-8650-4aa9-a35a-1deeba3e8991','ACTIVE','2025-04-16 05:42:55'),('d0610874-0301-4187-b24d-c8cbf02c0d96','test donation item','test donation item description','14661d75-8650-4aa9-a35a-1deeba3e8991','ACTIVE','2025-04-18 03:50:15');
/*!40000 ALTER TABLE `donation_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `donation_requests`
--

DROP TABLE IF EXISTS `donation_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `donation_requests` (
  `request_id` char(36) NOT NULL,
  `donation_id` char(36) NOT NULL,
  `requested_by` char(36) NOT NULL,
  `message` text,
  `status` varchar(20) DEFAULT 'PENDING',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`request_id`),
  KEY `fk_donation_id` (`donation_id`),
  KEY `fk_requested_by` (`requested_by`),
  CONSTRAINT `fk_donation_id` FOREIGN KEY (`donation_id`) REFERENCES `donation_items` (`donation_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_requested_by` FOREIGN KEY (`requested_by`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `donation_requests`
--

LOCK TABLES `donation_requests` WRITE;
/*!40000 ALTER TABLE `donation_requests` DISABLE KEYS */;
INSERT INTO `donation_requests` VALUES ('631b4543-9c4f-4018-a37b-fe7ac7dc8245','b030170e-1438-491b-93e5-69d813feec1e','2929166e-473e-43ba-8b51-ced6ed6dc42f','test massage websocket','PENDING','2025-05-04 01:28:10'),('845858dd-bdf5-4783-b6ed-614ba016c290','93bf71a7-3ee9-4e6e-9a3c-596c9ea9a686','14661d75-8650-4aa9-a35a-1deeba3e8991','first donation request from test@gmail.com','ACCEPTED','2025-05-03 13:21:28'),('8e767e38-e935-4cff-9737-2314266514bb','d0610874-0301-4187-b24d-c8cbf02c0d96','2929166e-473e-43ba-8b51-ced6ed6dc42f','I\'d like to request this donation.','PENDING','2025-05-04 01:38:43');
/*!40000 ALTER TABLE `donation_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_images`
--

DROP TABLE IF EXISTS `item_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_images` (
  `image_id` bigint NOT NULL AUTO_INCREMENT,
  `image_path` varchar(255) DEFAULT NULL,
  `item_id` char(36) DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `item_id` (`item_id`),
  CONSTRAINT `item_images_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_images`
--

LOCK TABLES `item_images` WRITE;
/*!40000 ALTER TABLE `item_images` DISABLE KEYS */;
INSERT INTO `item_images` VALUES (1,'Assets/16b2aeb4-f256-4755-a7be-cf2774556fda_53660210.jpg','b4689765-5ff6-4d92-8546-5b5caeb389d7'),(2,'Assets/2deca252-dc3a-4526-8953-3182ac7e5a1a_57374537.jpg','b4689765-5ff6-4d92-8546-5b5caeb389d7'),(3,'Assets/a430ffa1-b55b-4fad-ad08-874ca519a7e9_images (1).jpg','b4689765-5ff6-4d92-8546-5b5caeb389d7'),(4,'Assets/5fc66b40-74d3-4137-bb62-a1b8dc756fe3_images.jpg','b4689765-5ff6-4d92-8546-5b5caeb389d7'),(5,'Assets/444efa96-3a04-42b3-b292-167d313a2294_mini-9788194790839.jpg','b4689765-5ff6-4d92-8546-5b5caeb389d7'),(7,'Assets/eb89d957-592d-4347-a9a8-ce8d23caceb3_57374537.jpg','5c25a42c-680f-4e5c-bb86-9000e7614f96'),(8,'Assets/ce23dfac-1b30-438d-b3f5-7fb7a8c4ba06_images (1).jpg','5c25a42c-680f-4e5c-bb86-9000e7614f96'),(9,'Assets/dbe3eccd-05b6-4cbc-be22-19b161cca3fc_images.jpg','5c25a42c-680f-4e5c-bb86-9000e7614f96'),(10,'Assets/a2d352e4-2e00-47cd-b4c1-46282d721421_mini-9788194790839.jpg','5c25a42c-680f-4e5c-bb86-9000e7614f96'),(11,'Assets/f49163d4-f336-4b28-b27d-a68b5100f1a2_53660210.jpg','e3051010-eb9d-4997-95c1-08c45ed4c607'),(12,'Assets/fb33de5c-3b19-48fe-a53e-f586af323413_57374537.jpg','e3051010-eb9d-4997-95c1-08c45ed4c607'),(13,'Assets/3e8502e1-4bd6-4544-bbd0-51491204541d_images (1).jpg','e3051010-eb9d-4997-95c1-08c45ed4c607'),(14,'Assets/e61623b9-2c88-42cd-ac26-8b730ad96d82_images.jpg','e3051010-eb9d-4997-95c1-08c45ed4c607'),(15,'Assets/1c0f63b4-6683-4a78-9442-68b89d0ee7e8_mini-9788194790839.jpg','e3051010-eb9d-4997-95c1-08c45ed4c607'),(16,'Assets/20745503-6267-4c1a-b4f5-f10cd2208fd6_download (1).jpg','4e7f4835-81d2-4b64-b1f7-98eaefc0016b'),(17,'Assets/00789f4f-caca-446b-9491-ba633490a6f2_download (2).jpg','4e7f4835-81d2-4b64-b1f7-98eaefc0016b'),(18,'Assets/2c779f01-8369-46a3-96bc-f26e8cedb19f_download.jpg','4e7f4835-81d2-4b64-b1f7-98eaefc0016b'),(19,'Assets/6e60629d-6319-469c-aa7c-95ba7610f4fd_images (2).jpg','4e7f4835-81d2-4b64-b1f7-98eaefc0016b'),(20,'Assets/e6b811b9-c618-4bbd-8882-b6fb8ddf8f2a_images (3).jpg','4e7f4835-81d2-4b64-b1f7-98eaefc0016b'),(21,'Assets/8785df3f-544b-4ff0-b7a0-ff1854f832b0_1000000037.jpg','d9b6c816-f781-431a-a7e0-47761d4f942c'),(22,'Assets/668f7408-f607-463f-a2fb-dd6da723c653_1000000033.jpg','d9b6c816-f781-431a-a7e0-47761d4f942c'),(23,'Assets/8661dd73-69fc-4ac4-aa89-828cf464c6c9_1000000034.jpg','d9b6c816-f781-431a-a7e0-47761d4f942c'),(24,'Assets/6b8ec2de-ebd8-4c4d-aa18-c69206c138d9_1000000035.jpg','d9b6c816-f781-431a-a7e0-47761d4f942c'),(25,'Assets/97a3b8d2-8552-4fa0-9c70-5d4f6a1170a0_1000000036.jpg','d9b6c816-f781-431a-a7e0-47761d4f942c'),(27,'Assets/1569d04b-6235-4e11-b9ff-d0662f900f3d_download (11).jpg','557c37a4-0766-41bb-a868-9e00a8c1ab18'),(28,'Assets/afad9c0c-5c2a-413d-a512-01eee2013d92_download (12).jpg','557c37a4-0766-41bb-a868-9e00a8c1ab18'),(29,'Assets/c751c171-71a3-461c-ac3f-58cf21443d25_download (9).jpg','557c37a4-0766-41bb-a868-9e00a8c1ab18'),(30,'Assets/59bb18b2-6f7e-4aae-824e-f8b6c7bc9183_download (8).jpg','557c37a4-0766-41bb-a868-9e00a8c1ab18'),(31,'Assets/c6c66a18-f167-4de7-9e56-4175c084e5db_download (14).jpg','fabab09b-64b8-4723-b3b3-630c6adc6669'),(32,'Assets/769572c3-8944-4a4f-8084-32643e885cbf_download (15).jpg','fabab09b-64b8-4723-b3b3-630c6adc6669'),(33,'Assets/aab0ec80-5db1-4d34-aa5e-c3d1dbf9d0f6_download (16).jpg','fabab09b-64b8-4723-b3b3-630c6adc6669'),(34,'Assets/ee07d4ff-f954-414d-9acb-9e8af9639521_download (17).jpg','fabab09b-64b8-4723-b3b3-630c6adc6669'),(35,'Assets/63cf62ab-23c3-412e-a889-19807e043199_download (13).jpg','fabab09b-64b8-4723-b3b3-630c6adc6669'),(36,'Assets/96e04d4e-da7f-442a-9f4b-f6e59ba92758_images (5).jpg','4e441ebd-bc5e-44d1-8e4f-37c3051ec197'),(37,'Assets/555f5b19-56b6-4bbb-af03-d8c5e1f5c81f_Antonio-Strad-Dynasty-Violiin-Scarlet-12.jpg','4e441ebd-bc5e-44d1-8e4f-37c3051ec197'),(38,'Assets/1d611a1a-99d7-494c-8ada-a9486314ab90_Lazaro-Zucchi-Violin-20438.jpg','4e441ebd-bc5e-44d1-8e4f-37c3051ec197'),(39,'Assets/ebda44dd-6ae9-46a0-ab99-69081c8b623c_images (4).jpg','4e441ebd-bc5e-44d1-8e4f-37c3051ec197'),(40,'Assets/cd3855c8-9aad-475d-b408-945a0d9452d7_images (6).jpg','4e441ebd-bc5e-44d1-8e4f-37c3051ec197'),(41,'Assets/22e8a673-ae9d-46f9-bce6-d1836b8c913d_download (1).png','bfde73a5-475e-4d4a-86a6-2c36c2df751a'),(42,'Assets/b24f7592-d1ac-4fff-9cc2-67d50dbe235a_download (25).jpg','bfde73a5-475e-4d4a-86a6-2c36c2df751a'),(43,'Assets/751075bd-5fd7-4c69-b2a7-d78bba2de964_download (26).jpg','bfde73a5-475e-4d4a-86a6-2c36c2df751a'),(44,'Assets/93a00402-4aa1-446d-9533-ed199837cd36_download (27).jpg','bfde73a5-475e-4d4a-86a6-2c36c2df751a'),(45,'Assets/f3c08f0a-00d8-4f31-9b39-c876e692de06_download (28).jpg','bfde73a5-475e-4d4a-86a6-2c36c2df751a'),(46,'Assets/69ea401b-b564-433c-ac08-5c474e461a45_49.jpg','557c37a4-0766-41bb-a868-9e00a8c1ab18'),(47,'Assets/d123a88c-32f3-4abd-94ea-bc4e31d37182_36.jpg','5c25a42c-680f-4e5c-bb86-9000e7614f96');
/*!40000 ALTER TABLE `item_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
  `item_id` char(36) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `description` text,
  `price` decimal(10,2) DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `owner_id` char(36) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when item was created',
  PRIMARY KEY (`item_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `items_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES ('4e441ebd-bc5e-44d1-8e4f-37c3051ec197','Violin','Violins are important instruments in a wide variety of musical genres. They are most prominent in the Western classical tradition',5000.00,9,'bd3c5cc6-c386-4d0b-96b5-137a834e8646','ACTIVE','2025-05-02 12:20:16'),('4e7f4835-81d2-4b64-b1f7-98eaefc0016b','keyboard','Multi-system compatibility and portability: The 68-key keyboard is compatible with various computer systems, including Windows, Mac IOS, and Linux. It also has a compact and portable design, making it easy to carry and use.',100.00,1,'2929166e-473e-43ba-8b51-ced6ed6dc42f','ACTIVE','2025-04-09 06:04:44'),('557c37a4-0766-41bb-a868-9e00a8c1ab18','Dumbbell test','The dumbbell, a type of free weight, is a piece of equipment used in weight training',500.00,8,'14661d75-8650-4aa9-a35a-1deeba3e8991','ACTIVE','2025-04-09 10:03:38'),('5c25a42c-680f-4e5c-bb86-9000e7614f96','test','test',20.00,3,'14661d75-8650-4aa9-a35a-1deeba3e8991','ACTIVE','2025-04-09 06:04:44'),('b4689765-5ff6-4d92-8546-5b5caeb389d7','test','test',20.00,3,'14661d75-8650-4aa9-a35a-1deeba3e8991','ACTIVE','2025-04-09 06:04:44'),('bfde73a5-475e-4d4a-86a6-2c36c2df751a','Trumpet','Trumpet-like instruments have historically been used as signaling devices in battle or hunting',4000.00,9,'dea0be32-6ce3-4c69-8734-214ef0ed50d7','ACTIVE','2025-05-02 13:52:33'),('d9b6c816-f781-431a-a7e0-47761d4f942c','Guitar','The modern word guitar and its antecedents have been applied to a wide variety of chordophones since classical times, sometimes causing confusion',100.00,9,'2929166e-473e-43ba-8b51-ced6ed6dc42f','ACTIVE','2025-04-09 08:03:57'),('e3051010-eb9d-4997-95c1-08c45ed4c607','test','test',20.00,3,'14661d75-8650-4aa9-a35a-1deeba3e8991','ACTIVE','2025-04-09 06:04:44'),('fabab09b-64b8-4723-b3b3-630c6adc6669','Chair','Modern ergonomic wooden chair with cushioned seat, perfect for home or office use. Stylish, durable, and comfortable all day.',500.00,2,'2929166e-473e-43ba-8b51-ced6ed6dc42f','ACTIVE','2025-04-15 13:12:46');
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `notification_id` char(36) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `message` varchar(255) NOT NULL,
  `read` tinyint(1) DEFAULT '0',
  `reference_id` char(36) DEFAULT NULL,
  `reference_type` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notification_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES ('2c60033a-82fd-4c79-a47b-808ba9f99afb','14661d75-8650-4aa9-a35a-1deeba3e8991','Bhathika requested “test user donation”',0,'631b4543-9c4f-4018-a37b-fe7ac7dc8245','DONATION_REQUEST','2025-05-04 01:28:10'),('345baa43-d12a-4a8c-b199-f51bce6d30bb','2929166e-473e-43ba-8b51-ced6ed6dc42f','Bhathika requested to trade “Guitar”',0,'6cc61600-8543-4a8d-9657-f5165d93b8d4','TRADE_REQUEST','2025-05-03 18:25:20'),('357e3e32-ae83-4991-8edd-8cfc16d0c7a1','14661d75-8650-4aa9-a35a-1deeba3e8991','Bhathika accepted your request for “test update 2”',0,'845858dd-bdf5-4783-b6ed-614ba016c290','DONATION_REQUEST','2025-05-03 13:32:29'),('5d3bfa72-d34c-42ab-9026-50128c167f2c','2929166e-473e-43ba-8b51-ced6ed6dc42f','test requested “test update 2”',0,'845858dd-bdf5-4783-b6ed-614ba016c290','DONATION_REQUEST','2025-05-03 13:21:29'),('5fe4aeb7-28d8-45b6-92ba-fc4fe7703415','2929166e-473e-43ba-8b51-ced6ed6dc42f','test accepted your trade request for “Dumbbell”',0,'cb46ccd3-7602-45c6-a335-69880f0125a3','TRADE_REQUEST','2025-05-03 12:49:17'),('92f204e0-d7e6-4ea9-977f-89425e1c06ae','14661d75-8650-4aa9-a35a-1deeba3e8991','Bhathika requested to trade “Dumbbell”',1,'cb46ccd3-7602-45c6-a335-69880f0125a3','TRADE_REQUEST','2025-05-03 12:33:13'),('e6b2d46a-3651-4126-a592-2c54b26d0b91','14661d75-8650-4aa9-a35a-1deeba3e8991','Bhathika requested “test donation item”',0,'8e767e38-e935-4cff-9737-2314266514bb','DONATION_REQUEST','2025-05-04 01:38:43'),('fda2c39f-4c89-4770-aa9c-594e914b8be0','2929166e-473e-43ba-8b51-ced6ed6dc42f','test requested to trade “keyboard”',0,'97d7c327-6404-4ea2-ab0e-38b11af602b5','TRADE_REQUEST','2025-05-03 12:51:49');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ratings`
--

DROP TABLE IF EXISTS `ratings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ratings` (
  `rating_id` char(36) NOT NULL,
  `donation_request_id` char(36) NOT NULL,
  `rater_id` char(36) NOT NULL,
  `ratee_id` char(36) NOT NULL,
  `score` int NOT NULL,
  `comment` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`rating_id`),
  KEY `donation_request_id` (`donation_request_id`),
  KEY `rater_id` (`rater_id`),
  KEY `ratee_id` (`ratee_id`),
  CONSTRAINT `ratings_ibfk_1` FOREIGN KEY (`donation_request_id`) REFERENCES `donation_requests` (`request_id`),
  CONSTRAINT `ratings_ibfk_2` FOREIGN KEY (`rater_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `ratings_ibfk_3` FOREIGN KEY (`ratee_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ratings`
--

LOCK TABLES `ratings` WRITE;
/*!40000 ALTER TABLE `ratings` DISABLE KEYS */;
/*!40000 ALTER TABLE `ratings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trade_requests`
--

DROP TABLE IF EXISTS `trade_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trade_requests` (
  `request_id` char(36) NOT NULL,
  `item_id` char(36) NOT NULL,
  `offered_by` char(36) NOT NULL,
  `trade_type` enum('MONEY','ITEM') NOT NULL,
  `money_offer` decimal(10,2) DEFAULT '0.00',
  `receiver_selected_item_id` char(36) DEFAULT NULL,
  `status` enum('PENDING','ACCEPTED','REJECTED') DEFAULT 'PENDING',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`request_id`),
  KEY `item_id` (`item_id`),
  KEY `offered_by` (`offered_by`),
  KEY `receiver_selected_item_id` (`receiver_selected_item_id`),
  CONSTRAINT `trade_requests_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`),
  CONSTRAINT `trade_requests_ibfk_2` FOREIGN KEY (`offered_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `trade_requests_ibfk_3` FOREIGN KEY (`receiver_selected_item_id`) REFERENCES `items` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trade_requests`
--

LOCK TABLES `trade_requests` WRITE;
/*!40000 ALTER TABLE `trade_requests` DISABLE KEYS */;
INSERT INTO `trade_requests` VALUES ('97d7c327-6404-4ea2-ab0e-38b11af602b5','4e7f4835-81d2-4b64-b1f7-98eaefc0016b','14661d75-8650-4aa9-a35a-1deeba3e8991','ITEM',0.00,NULL,'PENDING','2025-05-03 12:51:49'),('cb46ccd3-7602-45c6-a335-69880f0125a3','557c37a4-0766-41bb-a868-9e00a8c1ab18','2929166e-473e-43ba-8b51-ced6ed6dc42f','ITEM',0.00,'d9b6c816-f781-431a-a7e0-47761d4f942c','ACCEPTED','2025-05-03 12:33:13');
/*!40000 ALTER TABLE `trade_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_profile_image`
--

DROP TABLE IF EXISTS `user_profile_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profile_image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` char(36) NOT NULL,
  `image_path` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_profile_image_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_profile_image`
--

LOCK TABLES `user_profile_image` WRITE;
/*!40000 ALTER TABLE `user_profile_image` DISABLE KEYS */;
INSERT INTO `user_profile_image` VALUES (1,'14661d75-8650-4aa9-a35a-1deeba3e8991','e2244c0b-5354-46f9-b084-a058996fb0fa_scaled_1000000043.png','2025-04-14 12:56:43'),(2,'2929166e-473e-43ba-8b51-ced6ed6dc42f','342c3fe1-d3ee-4052-af59-214b2c95f255_scaled_pngtree-man-avatar-image-for-profile-png-image_13001877.png','2025-04-15 03:04:54');
/*!40000 ALTER TABLE `user_profile_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` char(36) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `address` text,
  `city` varchar(100) DEFAULT NULL,
  `province` varchar(100) DEFAULT NULL,
  `is_verified` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('14661d75-8650-4aa9-a35a-1deeba3e8991','test','test@gmail.com','0771234567','$2a$10$JZfoazFThAEACUAGfvare.2XX.k1l/DTqKcOaDJCtsD5SZQcwalaa','test address','Colombo','Western',0,'2025-04-08 05:04:21'),('2929166e-473e-43ba-8b51-ced6ed6dc42f','Bhathika','bhathika@gmail.com','0761234567','$2a$10$G/F550xgZUFA8eNj9LojreJaf8Zh.RwaMmcHRyXGLfiCarbtRCjzO','235/Nugegoda','Colombo','Western',0,'2025-04-09 05:38:21'),('bd3c5cc6-c386-4d0b-96b5-137a834e8646','test2','test2@gmail.com','0978656384','$2a$10$sxbR8TXYTt1eH7fjJdDBj.xfatSUNxdiSl2lzJ5VdTZoAy4n4IPYK','test2address','puttalam','North-Western',0,'2025-04-15 05:12:23'),('dea0be32-6ce3-4c69-8734-214ef0ed50d7','test3','test3@gmail.com','0771928374','$2a$10$smazufxLssv1KA806BIaE.2hwaUr5TKLRbXXNLigAMgUau.auxVcO','122/A pangiriwatta,nugegoda','Nugegoda','Western',0,'2025-05-02 13:47:10');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-04 13:44:48
